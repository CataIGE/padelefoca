package be.ephec.backend.service;

import be.ephec.backend.config.JwtUtil;
import be.ephec.backend.dto.request.ConnexionAdminRequest;
import be.ephec.backend.dto.request.ConnexionJoueurRequest;
import be.ephec.backend.dto.response.ConnexionAdminResponse;
import be.ephec.backend.dto.response.ConnexionJoueurResponse;
import be.ephec.backend.exception.UnauthorizedException;
import be.ephec.backend.model.Administrateur;
import be.ephec.backend.model.Joueur;
import be.ephec.backend.repository.AdministrateurRepository;
import be.ephec.backend.repository.JoueurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JoueurRepository joueurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JoueurRepository joueurRepository,
                       AdministrateurRepository administrateurRepository,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder) {
        this.joueurRepository = joueurRepository;
        this.administrateurRepository = administrateurRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ConnexionJoueurResponse connexionJoueur(ConnexionJoueurRequest request) {
        Joueur joueur = joueurRepository.findByMatricule(request.getMatricule())
                .orElseThrow(() -> new UnauthorizedException("Matricule incorrect"));

        return new ConnexionJoueurResponse(
                joueur.getMatricule(),
                joueur.getNom(),
                joueur.getPrenom(),
                joueur.getTypeMembre()
        );
    }

    public ConnexionAdminResponse connexionAdmin(ConnexionAdminRequest request) {
        Administrateur admin = administrateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), admin.getMotDePasse())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getTypeAdmin().name());

        return new ConnexionAdminResponse(
                token,
                admin.getNom(),
                admin.getPrenom(),
                admin.getTypeAdmin(),
                admin.getSite() != null ? admin.getSite().getId() : null
        );
    }
}