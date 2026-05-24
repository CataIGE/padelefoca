package be.ephec.backend.service;

import be.ephec.backend.dto.response.StatistiqueGlobaleResponse;
import be.ephec.backend.dto.response.StatistiqueResponse;
import be.ephec.backend.dto.response.StatistiqueSiteResponse;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.Match;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.PaiementRepository;
import be.ephec.backend.repository.ReservationRepository;
import be.ephec.backend.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final PaiementRepository paiementRepository;
    private final MatchRepository matchRepository;
    private final JoueurRepository joueurRepository;
    private final ReservationRepository reservationRepository;
    private final SiteRepository siteRepository;

    public StatistiqueService(PaiementRepository paiementRepository,
                              MatchRepository matchRepository,
                              JoueurRepository joueurRepository,
                              ReservationRepository reservationRepository,
                              SiteRepository siteRepository) {
        this.paiementRepository = paiementRepository;
        this.matchRepository = matchRepository;
        this.joueurRepository = joueurRepository;
        this.reservationRepository = reservationRepository;
        this.siteRepository = siteRepository;
    }

    public StatistiqueResponse getStatistiquesGlobales() {
        double chiffreAffaires = paiementRepository.findAll()
                .stream()
                .mapToDouble(p -> p.getMontant())
                .sum();

        long nombreMatchsTermines = matchRepository.findAll()
                .stream()
                .filter(m -> m.getStatutMatch() == StatutMatch.TERMINE)
                .count();

        long nombreMatchsTotal = matchRepository.count();
        long nombreReservations = reservationRepository.count();
        double tauxRemplissage = nombreMatchsTotal == 0 ? 0 :
                (double) nombreReservations / (nombreMatchsTotal * 4) * 100;

        Map<String, Long> membresByType = joueurRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        j -> j.getTypeMembre().name(),
                        Collectors.counting()
                ));

        return new StatistiqueResponse(
                chiffreAffaires,
                tauxRemplissage,
                (int) nombreMatchsTermines,
                membresByType
        );
    }

    public StatistiqueResponse getStatistiquesBySite(Long siteId) {
        double chiffreAffaires = paiementRepository.findAll()
                .stream()
                .filter(p -> p.getReservation().getMatch()
                        .getTerrain().getSite().getId().equals(siteId))
                .mapToDouble(p -> p.getMontant())
                .sum();

        long nombreMatchsTermines = matchRepository.findByTerrainSiteId(siteId)
                .stream()
                .filter(m -> m.getStatutMatch() == StatutMatch.TERMINE)
                .count();

        long nombreMatchsSite = matchRepository.findByTerrainSiteId(siteId).size();
        double tauxRemplissage = nombreMatchsSite == 0 ? 0 :
                (double) nombreMatchsTermines / nombreMatchsSite * 100;

        Map<String, Long> membresByType = joueurRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        j -> j.getTypeMembre().name(),
                        Collectors.counting()
                ));

        return new StatistiqueResponse(
                chiffreAffaires,
                tauxRemplissage,
                (int) nombreMatchsTermines,
                membresByType
        );
    }

    public StatistiqueSiteResponse getStatistiquesSite(Long siteId) {
        be.ephec.backend.model.Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site introuvable"));

        double chiffreAffaires = paiementRepository.findAll()
                .stream()
                .filter(p -> p.getReservation().getMatch()
                        .getTerrain().getSite().getId().equals(siteId))
                .mapToDouble(p -> p.getMontant())
                .sum();

        List<Match> matchesSite = matchRepository.findByTerrainSiteId(siteId);

        long nombreMatchesTermines = matchesSite.stream()
                .filter(m -> m.getStatutMatch() == StatutMatch.TERMINE)
                .count();

        double tauxRemplissage = matchesSite.isEmpty() ? 0 :
                (double) nombreMatchesTermines / matchesSite.size() * 100;

        long nombreJoueurs = joueurRepository.findAll()
                .stream()
                .filter(j -> j.getSite() != null && j.getSite().getId().equals(siteId))
                .count();

        long nombreReservationsAnnee = reservationRepository.findAll()
                .stream()
                .filter(r -> r.getMatch().getTerrain().getSite().getId().equals(siteId))
                .count();

        return new StatistiqueSiteResponse(
                site.getId(),
                site.getNom(),
                chiffreAffaires,
                tauxRemplissage,
                (int) nombreMatchesTermines,
                (int) nombreJoueurs,
                (int) nombreReservationsAnnee
        );
    }

    public StatistiqueGlobaleResponse getStatistiquesGlobal() {
        double chiffreAffaires = paiementRepository.findAll()
                .stream()
                .mapToDouble(p -> p.getMontant())
                .sum();

        long nombreMatchesTermines = matchRepository.findAll()
                .stream()
                .filter(m -> m.getStatutMatch() == StatutMatch.TERMINE)
                .count();

        long nombreMatchesTotal = matchRepository.count();

        double tauxRemplissage = nombreMatchesTotal == 0 ? 0 :
                (double) nombreMatchesTermines / nombreMatchesTotal * 100;

        long nombreJoueursTotal = joueurRepository.count();

        long nombreReservationsAnnee = reservationRepository.count();

        long libres = joueurRepository.findAll().stream()
                .filter(j -> j.getTypeMembre() == TypeMembre.LIBRE).count();
        long site = joueurRepository.findAll().stream()
                .filter(j -> j.getTypeMembre() == TypeMembre.SITE).count();
        long globaux = joueurRepository.findAll().stream()
                .filter(j -> j.getTypeMembre() == TypeMembre.GLOBAL).count();

        return new StatistiqueGlobaleResponse(
                chiffreAffaires,
                tauxRemplissage,
                (int) nombreMatchesTermines,
                (int) nombreJoueursTotal,
                (int) nombreReservationsAnnee,
                new StatistiqueGlobaleResponse.RepartitionMembres(libres, site, globaux)
        );
    }
}