package be.ephec.backend.model;

import be.ephec.backend.model.enums.StatutPaiement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "joueur_id", nullable = false)
    private Joueur joueur;

    @Column(nullable = false)
    private double montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE;

    @Column(nullable = false)
    private LocalDateTime datePaiement;
}