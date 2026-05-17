package be.ephec.backend.repository;

import be.ephec.backend.model.Reservation;
import be.ephec.backend.model.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByJoueurId(Long joueurId);

    List<Reservation> findByMatchId(Long matchId);

    List<Reservation> findByMatchIdAndStatutReservation(Long matchId, StatutReservation statut);
}