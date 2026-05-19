package be.ephec.backend.service;

import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.StatutPaiement;
import be.ephec.backend.model.enums.StatutReservation;
import be.ephec.backend.model.enums.TypeMatch;
import be.ephec.backend.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchScheduler {

    private final MatchRepository matchRepository;
    private final ReservationRepository reservationRepository;
    private final PaiementRepository paiementRepository;
    private final JoueurRepository joueurRepository;

    public MatchScheduler(MatchRepository matchRepository,
                          ReservationRepository reservationRepository,
                          PaiementRepository paiementRepository,
                          JoueurRepository joueurRepository) {
        this.matchRepository = matchRepository;
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
        this.joueurRepository = joueurRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void verifierMatchsVeille() {
        LocalDateTime debutDemain = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime finDemain = debutDemain.plusDays(1);

        List<Match> matchsDemain = matchRepository.findByDateHeureBetween(debutDemain, finDemain);

        for (Match match : matchsDemain) {
            verifierPaiementsMatch(match);
        }
    }

    private void verifierPaiementsMatch(Match match) {
        List<Reservation> reservations = reservationRepository.findByMatchId(match.getId());

        for (Reservation reservation : reservations) {
            if (reservation.getStatutReservation() == StatutReservation.EN_ATTENTE) {
                boolean aPaye = paiementRepository
                        .findByReservationIdAndStatutPaiement(
                                reservation.getId(), StatutPaiement.PAYE)
                        .stream().findAny().isPresent();

                if (!aPaye) {
                    reservation.setStatutReservation(StatutReservation.ANNULEE);
                    reservationRepository.save(reservation);

                    boolean estOrganisateur = match.getOrganisateur().getId()
                            .equals(reservation.getJoueur().getId());

                    if (estOrganisateur && match.getTypeMatch() == TypeMatch.PRIVE) {
                        Joueur organisateur = reservation.getJoueur();
                        organisateur.setPenaliteActive(true);
                        organisateur.setFinPenalite(LocalDate.now().plusWeeks(1));
                        joueurRepository.save(organisateur);
                    }
                }
            }
        }

        List<Reservation> reservationsActives = reservationRepository
                .findByMatchIdAndStatutReservation(match.getId(), StatutReservation.EN_ATTENTE);

        if (match.getTypeMatch() == TypeMatch.PRIVE && !reservationsActives.isEmpty()) {
            match.setTypeMatch(TypeMatch.PUBLIC);
            matchRepository.save(match);
        }

        long placesOccupees = reservationRepository.findByMatchId(match.getId())
                .stream()
                .filter(r -> r.getStatutReservation() != StatutReservation.ANNULEE)
                .count();

        if (placesOccupees == 0) {
            match.setStatutMatch(StatutMatch.ANNULE);
        } else if (placesOccupees < Constantes.NOMBRE_JOUEURS_MAX) {
            match.setStatutMatch(StatutMatch.PLANIFIE);
        }
        matchRepository.save(match);
    }
}