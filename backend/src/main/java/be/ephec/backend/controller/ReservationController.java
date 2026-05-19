package be.ephec.backend.controller;

import be.ephec.backend.dto.response.ReservationResponse;
import be.ephec.backend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{matchId}")
    public ResponseEntity<ReservationResponse> reserverPlace(
            @PathVariable Long matchId,
            @RequestHeader("X-Matricule") String matricule) {
        return ResponseEntity.ok(reservationService.reserverPlace(matricule, matchId));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> annulerReservation(
            @PathVariable Long reservationId,
            @RequestHeader("X-Matricule") String matricule) {
        reservationService.annulerReservation(reservationId, matricule);
        return ResponseEntity.ok().build();
    }
}