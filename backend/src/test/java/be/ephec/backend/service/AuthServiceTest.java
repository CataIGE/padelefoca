package be.ephec.backend.service;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.config.JwtUtil;
import be.ephec.backend.dto.request.ConnexionAdminRequest;
import be.ephec.backend.dto.request.ConnexionJoueurRequest;
import be.ephec.backend.dto.response.ConnexionAdminResponse;
import be.ephec.backend.dto.response.ConnexionJoueurResponse;
import be.ephec.backend.exception.UnauthorizedException;
import be.ephec.backend.model.AdminGlobal;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.enums.TypeAdmin;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.AdministrateurRepository;
import be.ephec.backend.repository.JoueurRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthServiceTest extends AbstractUnitTest {

    @Mock
    private JoueurRepository joueurRepository;

    @Mock
    private AdministrateurRepository administrateurRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void connexionJoueur_devrait_retourner_le_joueur() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setNom("Dupont");
        joueur.setPrenom("Jean");
        joueur.setTypeMembre(TypeMembre.LIBRE);

        ConnexionJoueurRequest request = new ConnexionJoueurRequest();
        request.setMatricule("L0001");

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        ConnexionJoueurResponse response = authService.connexionJoueur(request);

        assertNotNull(response);
        assertEquals("L0001", response.getMatricule());
        assertEquals("Dupont", response.getNom());
    }

    @Test
    void connexionJoueur_devrait_lancer_exception_si_matricule_incorrect() {
        ConnexionJoueurRequest request = new ConnexionJoueurRequest();
        request.setMatricule("L9999");

        when(joueurRepository.findByMatricule("L9999")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.connexionJoueur(request));
    }

    @Test
    void connexionAdmin_devrait_retourner_token() {
        AdminGlobal admin = new AdminGlobal();
        admin.setEmail("admin@test.be");
        admin.setMotDePasse("hashedPassword");
        admin.setNom("Admin");
        admin.setPrenom("Global");
        admin.setTypeAdmin(TypeAdmin.GLOBAL);

        ConnexionAdminRequest request = new ConnexionAdminRequest();
        request.setEmail("admin@test.be");
        request.setMotDePasse("password123");

        when(administrateurRepository.findByEmail("admin@test.be")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin@test.be", "GLOBAL")).thenReturn("fake-jwt-token");

        ConnexionAdminResponse response = authService.connexionAdmin(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(TypeAdmin.GLOBAL, response.getTypeAdmin());
    }

    @Test
    void connexionAdmin_devrait_lancer_exception_si_mot_de_passe_incorrect() {
        AdminGlobal admin = new AdminGlobal();
        admin.setEmail("admin@test.be");
        admin.setMotDePasse("hashedPassword");
        admin.setTypeAdmin(TypeAdmin.GLOBAL);

        ConnexionAdminRequest request = new ConnexionAdminRequest();
        request.setEmail("admin@test.be");
        request.setMotDePasse("mauvaisMotDePasse");

        when(administrateurRepository.findByEmail("admin@test.be")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("mauvaisMotDePasse", "hashedPassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.connexionAdmin(request));
    }
}