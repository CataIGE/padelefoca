package be.ephec.backend.controller;

import be.ephec.backend.dto.request.PaiementRequest;
import be.ephec.backend.dto.response.PaiementResponse;
import be.ephec.backend.service.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    public ResponseEntity<PaiementResponse> payerMatch(
            @RequestHeader("X-Matricule") String matricule,
            @RequestBody PaiementRequest request) {
        return ResponseEntity.ok(paiementService.payerMatch(matricule, request));
    }

    @GetMapping
    public ResponseEntity<List<PaiementResponse>> getPaiements(
            @RequestHeader("X-Matricule") String matricule) {
        return ResponseEntity.ok(paiementService.getPaiementsByJoueur(matricule));
    }
}