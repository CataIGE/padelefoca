package be.ephec.backend.service;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.dto.request.CreerMatchRequest;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.Site;
import be.ephec.backend.model.Terrain;
import be.ephec.backend.model.enums.TypeMatch;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MatchServiceTest extends AbstractUnitTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private JoueurRepository joueurRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SiteService siteService;

    @Mock
    private TerrainService terrainService;

    @InjectMocks
    private MatchService matchService;

    @Test
    void creerMatch_devrait_lancer_exception_si_joueur_a_penalite() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(true);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        CreerMatchRequest request = new CreerMatchRequest();
        request.setSiteId(1L);
        request.setDateHeure(LocalDateTime.now().plusDays(3));
        request.setTypeMatch(TypeMatch.PUBLIC);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        assertThrows(BadRequestException.class,
                () -> matchService.creerMatch("L0001", request));
    }

    @Test
    void creerMatch_devrait_lancer_exception_si_joueur_a_solde_du() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(false);
        joueur.setSoldeDu(15.0);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        CreerMatchRequest request = new CreerMatchRequest();
        request.setSiteId(1L);
        request.setDateHeure(LocalDateTime.now().plusDays(3));
        request.setTypeMatch(TypeMatch.PUBLIC);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        assertThrows(BadRequestException.class,
                () -> matchService.creerMatch("L0001", request));
    }

    @Test
    void creerMatch_devrait_lancer_exception_si_site_ferme() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(false);
        joueur.setSoldeDu(0.0);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        CreerMatchRequest request = new CreerMatchRequest();
        request.setSiteId(1L);
        request.setDateHeure(LocalDateTime.now().plusDays(3));
        request.setTypeMatch(TypeMatch.PUBLIC);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));
        when(siteService.estOuvert(any(), any())).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> matchService.creerMatch("L0001", request));
    }
}