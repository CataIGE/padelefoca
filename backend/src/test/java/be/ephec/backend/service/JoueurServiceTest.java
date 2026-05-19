package be.ephec.backend.service;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.dto.request.InscriptionJoueurRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JoueurServiceTest extends AbstractUnitTest {

    @Mock
    private JoueurRepository joueurRepository;

    @InjectMocks
    private JoueurService joueurService;

    @Test
    void inscrire_devrait_creer_un_membre_libre() {
        InscriptionJoueurRequest request = new InscriptionJoueurRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@test.be");
        request.setTelephone("0477123456");

        when(joueurRepository.findAll()).thenReturn(List.of());
        when(joueurRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        JoueurResponse response = joueurService.inscrire(request);

        assertNotNull(response);
        assertEquals("Dupont", response.getNom());
        assertEquals("Jean", response.getPrenom());
        assertEquals(TypeMembre.LIBRE, response.getTypeMembre());
        assertTrue(response.getMatricule().startsWith("L"));
    }

    @Test
    void getProfil_devrait_retourner_le_joueur() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setNom("Dupont");
        joueur.setPrenom("Jean");
        joueur.setEmail("jean@test.be");
        joueur.setTypeMembre(TypeMembre.LIBRE);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        JoueurResponse response = joueurService.getProfil("L0001");

        assertNotNull(response);
        assertEquals("L0001", response.getMatricule());
        assertEquals("Dupont", response.getNom());
    }

    @Test
    void getProfil_devrait_lancer_exception_si_joueur_introuvable() {
        when(joueurRepository.findByMatricule("L9999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> joueurService.getProfil("L9999"));
    }
}