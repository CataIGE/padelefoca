package be.ephec.backend.repository;

import be.ephec.backend.model.Paiement;
import be.ephec.backend.model.enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByJoueurId(Long joueurId);

    List<Paiement> findByReservationId(Long reservationId);

    Optional<Paiement> findByReservationIdAndJoueurId(Long reservationId, Long joueurId);

    List<Paiement> findByReservationIdAndStatutPaiement(Long reservationId, StatutPaiement statut);
}