package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fermeture")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Fermeture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private String motif;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}