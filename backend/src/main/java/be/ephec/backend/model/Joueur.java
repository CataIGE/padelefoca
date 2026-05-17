package be.ephec.backend.model;

import be.ephec.backend.model.enums.TypeMembre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "joueur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMembre typeMembre;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    private boolean penaliteActive = false;

    private LocalDate finPenalite;

    @Column(nullable = false)
    private double soldeDu = 0.0;

    @Column(nullable = false)
    private double soldeCredit = 0.0;

    @Column(nullable = false)
    private int nombreReservationsSansPenalite = 0;

    public abstract int getDelaiReservation();
}