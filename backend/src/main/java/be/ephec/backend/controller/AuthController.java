package be.ephec.backend.controller;

import be.ephec.backend.dto.request.ConnexionAdminRequest;
import be.ephec.backend.dto.request.ConnexionJoueurRequest;
import be.ephec.backend.dto.response.ConnexionAdminResponse;
import be.ephec.backend.dto.response.ConnexionJoueurResponse;
import be.ephec.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/joueur/connexion")
    public ResponseEntity<ConnexionJoueurResponse> connexionJoueur(
            @RequestBody ConnexionJoueurRequest request) {
        return ResponseEntity.ok(authService.connexionJoueur(request));
    }

    @PostMapping("/admin/connexion")
    public ResponseEntity<ConnexionAdminResponse> connexionAdmin(
            @RequestBody ConnexionAdminRequest request) {
        return ResponseEntity.ok(authService.connexionAdmin(request));
    }
}