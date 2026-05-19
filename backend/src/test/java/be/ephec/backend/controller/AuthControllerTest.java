package be.ephec.backend.controller;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.dto.request.ConnexionAdminRequest;
import be.ephec.backend.dto.request.ConnexionJoueurRequest;
import be.ephec.backend.dto.response.ConnexionAdminResponse;
import be.ephec.backend.dto.response.ConnexionJoueurResponse;
import be.ephec.backend.model.enums.TypeAdmin;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest extends AbstractUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void connexionJoueur_devrait_retourner_200() {
        ConnexionJoueurRequest request = new ConnexionJoueurRequest();
        request.setMatricule("L0001");

        ConnexionJoueurResponse response = new ConnexionJoueurResponse(
                "L0001", "Dupont", "Jean", TypeMembre.LIBRE
        );

        when(authService.connexionJoueur(any())).thenReturn(response);

        ResponseEntity<ConnexionJoueurResponse> result = authController.connexionJoueur(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("L0001", result.getBody().getMatricule());
    }

    @Test
    void connexionAdmin_devrait_retourner_200_avec_token() {
        ConnexionAdminRequest request = new ConnexionAdminRequest();
        request.setEmail("admin@test.be");
        request.setMotDePasse("password123");

        ConnexionAdminResponse response = new ConnexionAdminResponse(
                "fake-jwt-token", "Admin", "Global",
                TypeAdmin.GLOBAL, null
        );

        when(authService.connexionAdmin(any())).thenReturn(response);

        ResponseEntity<ConnexionAdminResponse> result = authController.connexionAdmin(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("fake-jwt-token", result.getBody().getToken());
        assertEquals(TypeAdmin.GLOBAL, result.getBody().getTypeAdmin());
    }
}