package be.ephec.backend.service;

import be.ephec.backend.dto.response.StatistiqueResponse;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.MatchRepository;
import be.ephec.backend.repository.PaiementRepository;
import be.ephec.backend.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final PaiementRepository paiementRepository;
    private final MatchRepository matchRepository;
    private final JoueurRepository joueurRepository;
    private final ReservationRepository reservationRepository;

    public StatistiqueService(PaiementRepository paiementRepository,
                              MatchRepository matchRepository,
                              JoueurRepository joueurRepository,
                              ReservationRepository reservationRepository) {
        this.paiementRepository = paiementRepository;
        this.matchRepository = matchRepository;
        this.joueurRepository = joueurRepository;
        this.reservationRepository = reservationRepository;
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
        long nombreReservationsSite = nombreMatchsSite * 4;
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
}