package be.ephec.backend.service;

import be.ephec.backend.dto.request.PaiementRequest;
import be.ephec.backend.dto.response.PaiementResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.StatutPaiement;
import be.ephec.backend.model.enums.StatutReservation;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.PaiementRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final JoueurRepository joueurRepository;
    private final JoueurService joueurService;

    public PaiementService(PaiementRepository paiementRepository,
                           ReservationRepository reservationRepository,
                           JoueurRepository joueurRepository,
                           @Lazy JoueurService joueurService) {
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
        this.joueurRepository = joueurRepository;
        this.joueurService = joueurService;
    }

    public PaiementResponse payerMatch(String matricule, PaiementRequest request) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new NotFoundException("Réservation introuvable"));

        if (!reservation.getJoueur().getMatricule().equals(matricule)) {
            throw new BadRequestException("Cette réservation ne vous appartient pas");
        }

        if (reservation.getStatutReservation() == StatutReservation.ANNULEE) {
            throw new BadRequestException("Cette réservation est annulée");
        }

        boolean dejaPaye = paiementRepository.findByReservationIdAndJoueurId(
                reservation.getId(), joueur.getId()).isPresent();
        if (dejaPaye) {
            throw new BadRequestException("Vous avez déjà payé cette réservation");
        }

        double montant = Constantes.PRIX_PAR_JOUEUR;
        if (joueur.getSoldeCredit() > 0) {
            double creditUtilise = Math.min(joueur.getSoldeCredit(), montant);
            montant -= creditUtilise;
            joueur.setSoldeCredit(joueur.getSoldeCredit() - creditUtilise);
        }

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setJoueur(joueur);
        paiement.setMontant(montant);
        paiement.setStatutPaiement(StatutPaiement.PAYE);
        paiement.setDatePaiement(LocalDateTime.now());
        paiementRepository.save(paiement);

        reservation.setStatutReservation(StatutReservation.CONFIRMEE);
        reservationRepository.save(reservation);

        joueur.setSoldeDu(Math.max(0, joueur.getSoldeDu() - Constantes.PRIX_PAR_JOUEUR));
        joueur.setNombreReservationsSansPenalite(joueur.getNombreReservationsSansPenalite() + 1);
        joueurRepository.save(joueur);

        joueurService.verifierEtAppliquerPromotion(matricule);

        return toResponse(paiement);
    }

    public List<PaiementResponse> getPaiementsByJoueur(String matricule) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));
        return paiementRepository.findByJoueurId(joueur.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaiementResponse toResponse(Paiement paiement) {
        return new PaiementResponse(
                paiement.getId(),
                paiement.getJoueur().getMatricule(),
                paiement.getJoueur().getNom(),
                paiement.getReservation().getId(),
                paiement.getMontant(),
                paiement.getStatutPaiement(),
                paiement.getDatePaiement()
        );
    }
}