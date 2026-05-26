package be.ephec.backend.service;

import be.ephec.backend.dto.response.ReservationResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.StatutReservation;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import be.ephec.backend.model.enums.TypeMatch;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final JoueurRepository joueurRepository;
    private final MatchRepository matchRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              JoueurRepository joueurRepository,
                              MatchRepository matchRepository) {
        this.reservationRepository = reservationRepository;
        this.joueurRepository = joueurRepository;
        this.matchRepository = matchRepository;
    }

    public ReservationResponse reserverPlace(String matricule, Long matchId) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));

        if (joueur.isPenaliteActive()) {
            throw new BadRequestException("Vous avez une pénalité active");
        }

        if (joueur.getSoldeDu() > 0) {
            throw new BadRequestException("Vous avez un solde dû impayé");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match introuvable"));

        if (match.getStatutMatch() != StatutMatch.PLANIFIE) {
            throw new BadRequestException("Ce match n'est plus disponible");
        }

        LocalDate dateMatch = match.getDateHeure().toLocalDate();
        LocalDate dateLimite = LocalDate.now().plusDays(joueur.getDelaiReservation());
        if (dateMatch.isAfter(dateLimite)) {
            throw new BadRequestException("Vous ne pouvez pas réserver ce match aussi tôt — délai de "
                    + joueur.getDelaiReservation() + " jours maximum");
        }

        List<Reservation> reservationsExistantes = reservationRepository.findByMatchId(matchId);
        if (reservationsExistantes.size() >= Constantes.NOMBRE_JOUEURS_MAX) {
            throw new BadRequestException("Ce match est complet");
        }

        Reservation reservation = new Reservation();
        reservation.setJoueur(joueur);
        reservation.setMatch(match);
        reservation.setStatutReservation(StatutReservation.EN_ATTENTE);
        reservationRepository.save(reservation);

        joueur.setSoldeDu(joueur.getSoldeDu() + Constantes.PRIX_PAR_JOUEUR);
        joueurRepository.save(joueur);

        if (reservationsExistantes.size() + 1 == Constantes.NOMBRE_JOUEURS_MAX) {
            match.setStatutMatch(StatutMatch.COMPLET);
            matchRepository.save(match);
        }

        return toResponse(reservation);
    }

    public List<ReservationResponse> getReservationsByJoueur(String matricule) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));
        return reservationRepository.findByJoueurId(joueur.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void annulerReservation(Long reservationId, String matricule) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable"));

        if (!reservation.getJoueur().getMatricule().equals(matricule)) {
            throw new BadRequestException("Vous ne pouvez pas annuler la réservation d'un autre joueur");
        }

        Joueur joueur = reservation.getJoueur();

        if (reservation.getStatutReservation() == StatutReservation.CONFIRMEE) {
            // Réservation payée → rembourser en crédit
            joueur.setSoldeCredit(joueur.getSoldeCredit() + Constantes.PRIX_PAR_JOUEUR);
        } else if (reservation.getStatutReservation() == StatutReservation.EN_ATTENTE) {
            // Réservation non payée → enlever du soldeDu
            joueur.setSoldeDu(Math.max(0, joueur.getSoldeDu() - Constantes.PRIX_PAR_JOUEUR));
        }
        joueurRepository.save(joueur);

        reservation.setStatutReservation(StatutReservation.ANNULEE);
        reservationRepository.save(reservation);

        Match match = reservation.getMatch();
        if (match.getStatutMatch() == StatutMatch.COMPLET) {
            match.setTypeMatch(TypeMatch.PUBLIC);
            match.setStatutMatch(StatutMatch.PLANIFIE);
            matchRepository.save(match);
        }
    }

    public ReservationResponse toResponse(Reservation reservation) {
        double montantDu = Constantes.PRIX_PAR_JOUEUR - reservation.getJoueur().getSoldeCredit();
        montantDu = Math.max(0, montantDu);

        return new ReservationResponse(
                reservation.getId(),
                reservation.getJoueur().getMatricule(),
                reservation.getJoueur().getNom(),
                reservation.getMatch().getId(),
                reservation.getMatch().getTerrain().getSite().getNom(),
                reservation.getMatch().getTerrain().getNom(),
                reservation.getMatch().getDateHeure(),
                reservation.getStatutReservation(),
                montantDu
        );
    }
}