package be.ephec.backend.controller;

import be.ephec.backend.dto.request.InscriptionJoueurRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.dto.response.ReservationResponse;
import be.ephec.backend.service.JoueurService;
import be.ephec.backend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joueurs")
public class JoueurController {

    private final JoueurService joueurService;
    private final ReservationService reservationService;

    public JoueurController(JoueurService joueurService,
                            ReservationService reservationService) {
        this.joueurService = joueurService;
        this.reservationService = reservationService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<JoueurResponse> inscrire(
            @RequestBody InscriptionJoueurRequest request) {
        return ResponseEntity.ok(joueurService.inscrire(request));
    }

    @GetMapping("/profil")
    public ResponseEntity<JoueurResponse> getProfil(
            @RequestHeader("X-Matricule") String matricule) {
        return ResponseEntity.ok(joueurService.getProfil(matricule));
    }

    @GetMapping("/{matricule}")
    public ResponseEntity<JoueurResponse> getJoueurByMatricule(
            @PathVariable String matricule) {
        return ResponseEntity.ok(joueurService.getProfil(matricule));
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestHeader("X-Matricule") String matricule) {
        return ResponseEntity.ok(reservationService.getReservationsByJoueur(matricule));
    }
}