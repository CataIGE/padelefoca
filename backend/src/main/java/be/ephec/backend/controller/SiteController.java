package be.ephec.backend.controller;

import be.ephec.backend.dto.response.SiteResponse;
import be.ephec.backend.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<LocalTime>> getCreneaux(
            @PathVariable Long siteId,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        if (!siteService.estOuvert(siteId, localDate)) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(siteService.genererCreneaux(siteId));
    }
}