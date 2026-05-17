package be.ephec.backend.model;

import be.ephec.backend.model.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "joueur_id", nullable = false)
    private Joueur joueur;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statutReservation = StatutReservation.EN_ATTENTE;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
}