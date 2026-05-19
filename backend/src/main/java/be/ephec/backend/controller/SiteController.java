package be.ephec.backend.controller;

import be.ephec.backend.dto.response.SiteResponse;
import be.ephec.backend.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import be.ephec.backend.dto.response.CreneauResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public ResponseEntity<List<SiteResponse>> getTousLesSites() {
        return ResponseEntity.ok(siteService.getTousLesSites());
    }

    @GetMapping("/{siteId}")
    public ResponseEntity<SiteResponse> getSiteById(@PathVariable Long siteId) {
        return ResponseEntity.ok(siteService.getSiteById(siteId));
    }

    @GetMapping("/{siteId}/creneaux")
    public ResponseEntity<List<CreneauResponse>> getCreneaux(
            @PathVariable Long siteId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(siteService.genererCreneauxAvecStatut(siteId, localDate));
    }
}