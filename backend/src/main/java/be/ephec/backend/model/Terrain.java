package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terrain")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Terrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;
}