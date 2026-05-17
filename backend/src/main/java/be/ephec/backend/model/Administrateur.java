package be.ephec.backend.model;

import be.ephec.backend.model.enums.TypeAdmin;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_admin", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Administrateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAdmin typeAdmin;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}