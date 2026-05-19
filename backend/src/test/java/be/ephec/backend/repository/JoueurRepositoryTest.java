package be.ephec.backend.repository;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.enums.TypeMembre;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JoueurRepositoryTest extends AbstractUnitTest {

    @Mock
    private JoueurRepository joueurRepository;

    @Test
    void findByMatricule_devrait_retourner_le_joueur() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setNom("Dupont");
        joueur.setPrenom("Jean");
        joueur.setEmail("jean@test.be");
        joueur.setTypeMembre(TypeMembre.LIBRE);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        Optional<be.ephec.backend.model.Joueur> result = joueurRepository.findByMatricule("L0001");

        assertTrue(result.isPresent());
        assertEquals("Dupont", result.get().getNom());
    }

    @Test
    void existsByMatricule_devrait_retourner_true_si_existe() {
        when(joueurRepository.existsByMatricule("L0001")).thenReturn(true);
        when(joueurRepository.existsByMatricule("L9999")).thenReturn(false);

        assertTrue(joueurRepository.existsByMatricule("L0001"));
        assertFalse(joueurRepository.existsByMatricule("L9999"));
    }
}