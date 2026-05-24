package be.ephec.backend.controller;

import be.ephec.backend.dto.request.CreerAdminRequest;
import be.ephec.backend.dto.request.ModifierAdminRequest;
import be.ephec.backend.dto.response.AdminResponse;
import be.ephec.backend.service.GestionAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class GestionAdminController {

    private final GestionAdminService gestionAdminService;

    public GestionAdminController(GestionAdminService gestionAdminService) {
        this.gestionAdminService = gestionAdminService;
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getTousLesAdmins() {
        return ResponseEntity.ok(gestionAdminService.getTousLesAdmins());
    }

    @PostMapping
    public ResponseEntity<AdminResponse> creerAdmin(
            @RequestBody CreerAdminRequest request) {
        return ResponseEntity.ok(gestionAdminService.creerAdmin(request));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<AdminResponse> modifierAdmin(
            @PathVariable Long adminId,
            @RequestBody ModifierAdminRequest request) {
        return ResponseEntity.ok(gestionAdminService.modifierAdmin(adminId, request));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> supprimerAdmin(
            @PathVariable Long adminId) {
        gestionAdminService.supprimerAdmin(adminId);
        return ResponseEntity.ok().build();
    }
}