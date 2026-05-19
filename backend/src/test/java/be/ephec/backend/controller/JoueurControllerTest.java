package be.ephec.backend.controller;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.dto.request.InscriptionJoueurRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.service.JoueurService;
import be.ephec.backend.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JoueurControllerTest extends AbstractUnitTest {

    @Mock
    private JoueurService joueurService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private JoueurController joueurController;

    @Test
    void inscrire_devrait_retourner_200() {
        InscriptionJoueurRequest request = new InscriptionJoueurRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@test.be");

        JoueurResponse response = new JoueurResponse(
                1L, "L0001", "Dupont", "Jean",
                "jean@test.be", null, TypeMembre.LIBRE,
                false, 0.0, 0.0
        );

        when(joueurService.inscrire(any())).thenReturn(response);

        ResponseEntity<JoueurResponse> result = joueurController.inscrire(request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals("L0001", result.getBody().getMatricule());
    }

    @Test
    void getProfil_devrait_retourner_le_joueur() {
        JoueurResponse response = new JoueurResponse(
                1L, "L0001", "Dupont", "Jean",
                "jean@test.be", null, TypeMembre.LIBRE,
                false, 0.0, 0.0
        );

        when(joueurService.getProfil("L0001")).thenReturn(response);

        ResponseEntity<JoueurResponse> result = joueurController.getProfil("L0001");

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Dupont", result.getBody().getNom());
    }
}