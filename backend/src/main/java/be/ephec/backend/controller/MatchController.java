package be.ephec.backend.controller;

import be.ephec.backend.dto.request.AjouterJoueurMatchRequest;
import be.ephec.backend.dto.request.CreerMatchRequest;
import be.ephec.backend.dto.response.MatchResponse;
import be.ephec.backend.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getMatchsPublicsOuverts() {
        return ResponseEntity.ok(matchService.getMatchsPublicsOuverts());
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getMatchById(matchId));
    }

    @PostMapping
    public ResponseEntity<MatchResponse> creerMatch(
            @RequestHeader("X-Matricule") String matricule,
            @RequestBody CreerMatchRequest request) {
        return ResponseEntity.ok(matchService.creerMatch(matricule, request));
    }

    @PostMapping("/{matchId}/joueurs")
    public ResponseEntity<Void> ajouterJoueurs(
            @PathVariable Long matchId,
            @RequestBody AjouterJoueurMatchRequest request) {
        matchService.ajouterJoueurs(matchId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> annulerMatch(
            @PathVariable Long matchId,
            @RequestHeader("X-Matricule") String matricule) {
        matchService.annulerMatch(matchId, matricule);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<MatchResponse>> getMatchsBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(matchService.getMatchsBySite(siteId));
    }
}