package be.ephec.backend.service;

import be.ephec.backend.dto.response.CreneauResponse;
import be.ephec.backend.dto.response.SiteResponse;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMatch;
import be.ephec.backend.repository.FermetureRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.ReservationRepository;
import be.ephec.backend.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final FermetureRepository fermetureRepository;
    private final MatchRepository matchRepository;
    private final ReservationRepository reservationRepository;

    public SiteService(SiteRepository siteRepository,
                       FermetureRepository fermetureRepository,
                       MatchRepository matchRepository,
                       ReservationRepository reservationRepository) {
        this.siteRepository = siteRepository;
        this.fermetureRepository = fermetureRepository;
        this.matchRepository = matchRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<SiteResponse> getTousLesSites() {
        return siteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SiteResponse getSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Site avec id " + id + " introuvable"));
        return toResponse(site);
    }

    public boolean estOuvert(Long siteId, LocalDate date) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site introuvable"));

        if (site.getJoursRepos().contains(date.getDayOfWeek())) {
            return false;
        }

        List<?> fermetures = fermetureRepository.findFermeturesActives(date, siteId);
        return fermetures.isEmpty();
    }

    public List<CreneauResponse> genererCreneauxAvecStatut(Long siteId, LocalDate date) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site introuvable"));

        if (!estOuvert(siteId, date)) {
            return List.of();
        }

        List<CreneauResponse> creneaux = new ArrayList<>();
        LocalTime heure = site.getHeureOuverture();
        int dureeSlot = Constantes.DUREE_MATCH + Constantes.PAUSE_MATCH;

        while (heure.plusMinutes(Constantes.DUREE_MATCH)
                .isBefore(site.getHeureFermeture().plusSeconds(1))) {

            LocalDateTime dateHeure = date.atTime(heure);
            List<Match> matchesCreneau = matchRepository.findByDateHeureBetween(
                    dateHeure.minusMinutes(1), dateHeure.plusMinutes(1));

            String statut = "LIBRE";
            Long matchId = null;
            int placesDisponibles = Constantes.NOMBRE_JOUEURS_MAX;

            if (!matchesCreneau.isEmpty()) {
                Match match = matchesCreneau.get(0);
                matchId = match.getId();
                long nbReservations = reservationRepository.findByMatchId(match.getId())
                        .stream()
                        .filter(r -> r.getStatutReservation() != be.ephec.backend.model.enums.StatutReservation.ANNULEE)
                        .count();
                placesDisponibles = (int) (Constantes.NOMBRE_JOUEURS_MAX - nbReservations);

                if (match.getStatutMatch() == StatutMatch.COMPLET) {
                    statut = "COMPLET";
                } else if (match.getTypeMatch() == TypeMatch.PUBLIC) {
                    statut = "MATCH_PUBLIC";
                } else {
                    statut = "MATCH_PRIVE";
                }
            }

            creneaux.add(new CreneauResponse(heure.toString(), statut, matchId, placesDisponibles));
            heure = heure.plusMinutes(dureeSlot);
        }

        return creneaux;
    }

    public SiteResponse toResponse(Site site) {
        return new SiteResponse(
                site.getId(),
                site.getNom(),
                site.getAdresse(),
                site.getHeureOuverture(),
                site.getHeureFermeture(),
                site.getJoursRepos()
        );
    }
}