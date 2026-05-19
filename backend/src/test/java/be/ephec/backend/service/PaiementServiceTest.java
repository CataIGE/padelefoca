package be.ephec.backend.service;

import be.ephec.backend.AbstractUnitTest;
import be.ephec.backend.dto.request.PaiementRequest;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.Paiement;
import be.ephec.backend.model.Reservation;
import be.ephec.backend.model.enums.StatutPaiement;
import be.ephec.backend.model.enums.StatutReservation;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.PaiementRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class PaiementServiceTest extends AbstractUnitTest {

    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private JoueurRepository joueurRepository;

    @InjectMocks
    private PaiementService paiementService;

    @Test
    void payerMatch_devrait_lancer_exception_si_reservation_annulee() {
        MembreLibre joueur = new MembreLibre();
        joueur.setId(1L);
        joueur.setMatricule("L0001");
        joueur.setTypeMembre(TypeMembre.LIBRE);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setJoueur(joueur);
        reservation.setStatutReservation(StatutReservation.ANNULEE);

        PaiementRequest request = new PaiementRequest();
        request.setReservationId(1L);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(BadRequestException.class,
                () -> paiementService.payerMatch("L0001", request));
    }

    @Test
    void payerMatch_devrait_lancer_exception_si_deja_paye() {
        MembreLibre joueur = new MembreLibre();
        joueur.setId(1L);
        joueur.setMatricule("L0001");
        joueur.setTypeMembre(TypeMembre.LIBRE);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setJoueur(joueur);
        reservation.setStatutReservation(StatutReservation.EN_ATTENTE);

        Paiement paiement = new Paiement();
        paiement.setStatutPaiement(StatutPaiement.PAYE);

        PaiementRequest request = new PaiementRequest();
        request.setReservationId(1L);

        when(joueurRepository.findByMatricule("L0001")).thenReturn(Optional.of(joueur));
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paiementRepository.findByReservationIdAndJoueurId(1L, 1L))
                .thenReturn(Optional.of(paiement));

        assertThrows(BadRequestException.class,
                () -> paiementService.payerMatch("L0001", request));
    }
}