package be.ephec.backend.service;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.model.Match;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.Reservation;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ReservationServiceTest extends AbstractUnitTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private JoueurRepository joueurRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void reserverPlace_devrait_lancer_exception_si_joueur_a_penalite() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(true);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));

        assertThrows(BadRequestException.class,
                () -> reservationService.reserverPlace("L0001", 1L));
    }

    @Test
    void reserverPlace_devrait_lancer_exception_si_match_complet() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(false);
        joueur.setSoldeDu(0.0);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        Match match = new Match();
        match.setStatutMatch(StatutMatch.PLANIFIE);
        match.setDateHeure(LocalDateTime.now().plusDays(2));

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(reservationRepository.findByMatchId(1L)).thenReturn(
                List.of(new Reservation(), new Reservation(),
                        new Reservation(), new Reservation()));

        assertThrows(BadRequestException.class,
                () -> reservationService.reserverPlace("L0001", 1L));
    }

    @Test
    void reserverPlace_devrait_lancer_exception_si_match_annule() {
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule("L0001");
        joueur.setPenaliteActive(false);
        joueur.setSoldeDu(0.0);
        joueur.setTypeMembre(TypeMembre.LIBRE);

        Match match = new Match();
        match.setStatutMatch(StatutMatch.ANNULE);
        match.setDateHeure(LocalDateTime.now().plusDays(2));

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        assertThrows(BadRequestException.class,
                () -> reservationService.reserverPlace("L0001", 1L));
    }
}