package be.ephec.backend.repository;

import be.ephec.backend.model.Reservation;
import be.ephec.backend.model.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByJoueurId(Long joueurId);

    List<Reservation> findByMatchId(Long matchId);

    List<Reservation> findByMatchIdAndStatutReservation(Long matchId, StatutReservation statut);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.joueur.id = :joueurId AND r.match.terrain.site.id = :siteId AND r.statutReservation = be.ephec.backend.model.enums.StatutReservation.CONFIRMEE")
    long countConfirmeesByJoueurIdAndSiteId(@Param("joueurId") Long joueurId, @Param("siteId") Long siteId);
}