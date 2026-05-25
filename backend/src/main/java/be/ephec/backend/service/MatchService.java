package be.ephec.backend.service;

import be.ephec.backend.dto.request.AjouterJoueurMatchRequest;
import be.ephec.backend.dto.request.CreerMatchRequest;
import be.ephec.backend.dto.response.MatchResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMatch;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final JoueurRepository joueurRepository;
    private final ReservationRepository reservationRepository;
    private final SiteService siteService;
    private final TerrainService terrainService;

    public MatchService(MatchRepository matchRepository,
                        JoueurRepository joueurRepository,
                        ReservationRepository reservationRepository,
                        SiteService siteService,
                        TerrainService terrainService) {
        this.matchRepository = matchRepository;
        this.joueurRepository = joueurRepository;
        this.reservationRepository = reservationRepository;
        this.siteService = siteService;
        this.terrainService = terrainService;
    }

    public MatchResponse creerMatch(String matriculeOrganisateur, CreerMatchRequest request) {
        Joueur organisateur = joueurRepository.findByMatricule(matriculeOrganisateur)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));

        if (organisateur.isPenaliteActive()) {
            throw new BadRequestException("Vous avez une pénalité active — vous ne pouvez pas créer de match");
        }

        if (organisateur.getSoldeDu() > 0) {
            throw new BadRequestException("Vous avez un solde dû impayé — vous ne pouvez pas créer de match");
        }

        if (!siteService.estOuvert(request.getSiteId(), request.getDateHeure().toLocalDate())) {
            throw new BadRequestException("Le site est fermé à cette date");
        }

        Terrain terrain = terrainService.getTerrainDisponible(request.getSiteId(), request.getDateHeure());

        Match match = new Match();
        match.setTerrain(terrain);
        match.setOrganisateur(organisateur);
        match.setDateHeure(request.getDateHeure());
        match.setTypeMatch(request.getTypeMatch());
        match.setStatutMatch(StatutMatch.PLANIFIE);

        matchRepository.save(match);

        Reservation reservation = new Reservation();
        reservation.setMatch(match);
        reservation.setJoueur(organisateur);
        reservation.setStatutReservation(be.ephec.backend.model.enums.StatutReservation.EN_ATTENTE);
        reservationRepository.save(reservation);

        return toResponse(match);
    }

    public MatchResponse getMatchById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Match avec id " + id + " introuvable"));
        return toResponse(match);
    }

    public List<MatchResponse> getMatchsPublicsOuverts() {
        return matchRepository.findByTypeMatchAndStatutMatch(TypeMatch.PUBLIC, StatutMatch.PLANIFIE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MatchResponse> getMatchsBySite(Long siteId) {
        return matchRepository.findByTerrainSiteId(siteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void ajouterJoueurs(Long matchId, AjouterJoueurMatchRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match introuvable"));

        if (match.getStatutMatch() != StatutMatch.PLANIFIE) {
            throw new BadRequestException("Ce match n'est plus disponible");
        }

        List<Reservation> reservationsExistantes = reservationRepository.findByMatchId(matchId);
        int placesOccupees = reservationsExistantes.size();

        if (placesOccupees + request.getMatricules().size() > Constantes.NOMBRE_JOUEURS_MAX) {
            throw new BadRequestException("Pas assez de places disponibles dans ce match");
        }

        for (String matricule : request.getMatricules()) {
            Joueur joueur = joueurRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new NotFoundException("Joueur avec matricule " + matricule + " introuvable"));

            boolean dejaInscrit = reservationsExistantes.stream()
                    .anyMatch(r -> r.getJoueur().getMatricule().equals(matricule));

            if (dejaInscrit) {
                continue;
            }

            Reservation reservation = new Reservation();
            reservation.setMatch(match);
            reservation.setJoueur(joueur);
            reservation.setStatutReservation(be.ephec.backend.model.enums.StatutReservation.EN_ATTENTE);
            reservationRepository.save(reservation);
        }

        if (placesOccupees + request.getMatricules().size() == Constantes.NOMBRE_JOUEURS_MAX) {
            match.setStatutMatch(StatutMatch.COMPLET);
            matchRepository.save(match);
        }
    }

    public void annulerMatch(Long matchId, String matricule) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match introuvable"));

        if (!match.getOrganisateur().getMatricule().equals(matricule)) {
            throw new BadRequestException("Seul l'organisateur peut annuler le match");
        }

        match.setStatutMatch(StatutMatch.ANNULE);
        matchRepository.save(match);
    }

    public MatchResponse toResponse(Match match) {
        List<Reservation> reservations = reservationRepository.findByMatchId(match.getId());
        int nombreJoueurs = reservations.size();
        List<String> matricules = reservations.stream()
                .map(r -> r.getJoueur().getMatricule())
                .toList();

        return new MatchResponse(
                match.getId(),
                match.getTerrain().getSite().getId(),
                match.getTerrain().getSite().getNom(),
                match.getTerrain().getId(),
                match.getTerrain().getNom(),
                match.getOrganisateur().getMatricule(),
                match.getOrganisateur().getNom(),
                match.getDateHeure(),
                match.getTypeMatch(),
                match.getStatutMatch(),
                nombreJoueurs,
                Constantes.NOMBRE_JOUEURS_MAX - nombreJoueurs,
                matricules
        );
    }

    public void rejoindreMatchPublic(String matricule, Long matchId) {
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

        if (match.getTypeMatch() != TypeMatch.PUBLIC) {
            throw new BadRequestException("Ce match n'est pas public");
        }

        if (match.getStatutMatch() != StatutMatch.PLANIFIE) {
            throw new BadRequestException("Ce match n'est plus disponible");
        }

        List<Reservation> reservationsExistantes = reservationRepository.findByMatchId(matchId);

        if (reservationsExistantes.size() >= Constantes.NOMBRE_JOUEURS_MAX) {
            throw new BadRequestException("Ce match est complet");
        }

        boolean dejaInscrit = reservationsExistantes.stream()
                .anyMatch(r -> r.getJoueur().getMatricule().equals(matricule));

        if (dejaInscrit) {
            throw new BadRequestException("Vous êtes déjà inscrit à ce match");
        }

        Reservation reservation = new Reservation();
        reservation.setMatch(match);
        reservation.setJoueur(joueur);
        reservation.setStatutReservation(be.ephec.backend.model.enums.StatutReservation.EN_ATTENTE);
        reservationRepository.save(reservation);

        if (reservationsExistantes.size() + 1 == Constantes.NOMBRE_JOUEURS_MAX) {
            match.setStatutMatch(StatutMatch.COMPLET);
            matchRepository.save(match);
        }
    }
}