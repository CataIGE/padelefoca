package be.ephec.backend.controller;

import be.ephec.backend.dto.request.FermetureRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.dto.response.MatchResponse;
import be.ephec.backend.dto.response.StatistiqueResponse;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.Administrateur;
import be.ephec.backend.repository.AdministrateurRepository;
import be.ephec.backend.service.AdminService;
import be.ephec.backend.service.StatistiqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import be.ephec.backend.dto.response.StatistiqueGlobaleResponse;
import be.ephec.backend.dto.response.StatistiqueSiteResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StatistiqueService statistiqueService;
    private final AdministrateurRepository administrateurRepository;

    public AdminController(AdminService adminService,
                           StatistiqueService statistiqueService,
                           AdministrateurRepository administrateurRepository) {
        this.adminService = adminService;
        this.statistiqueService = statistiqueService;
        this.administrateurRepository = administrateurRepository;
    }

    @GetMapping("/joueurs")
    public ResponseEntity<List<JoueurResponse>> getJoueurs(
            @RequestHeader("X-Admin-Email") String email) {
        Administrateur admin = administrateurRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));
        return ResponseEntity.ok(adminService.getJoueurs(admin));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponse>> getMatchs(
            @RequestHeader("X-Admin-Email") String email) {
        Administrateur admin = administrateurRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));
        return ResponseEntity.ok(adminService.getMatchs(admin));
    }

    @GetMapping("/statistiques")
    public ResponseEntity<StatistiqueResponse> getStatistiques(
            @RequestHeader("X-Admin-Email") String email) {
        Administrateur admin = administrateurRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));
        if (admin.getSite() != null) {
            return ResponseEntity.ok(statistiqueService.getStatistiquesBySite(admin.getSite().getId()));
        }
        return ResponseEntity.ok(statistiqueService.getStatistiquesGlobales());
    }

    @PostMapping("/fermetures")
    public ResponseEntity<Void> ajouterFermeture(
            @RequestBody FermetureRequest request) {
        adminService.ajouterFermeture(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/fermetures/{fermetureId}")
    public ResponseEntity<Void> supprimerFermeture(
            @PathVariable Long fermetureId) {
        adminService.supprimerFermeture(fermetureId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistiques/global")
    public ResponseEntity<StatistiqueGlobaleResponse> getStatistiquesGlobal() {
        return ResponseEntity.ok(statistiqueService.getStatistiquesGlobal());
    }

    @GetMapping("/statistiques/site/{siteId}")
    public ResponseEntity<StatistiqueSiteResponse> getStatistiquesSite(
            @PathVariable Long siteId) {
        return ResponseEntity.ok(statistiqueService.getStatistiquesSite(siteId));
    }
}