package be.ephec.backend.service;

import be.ephec.backend.dto.response.SiteResponse;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.Site;
import be.ephec.backend.repository.FermetureRepository;
import be.ephec.backend.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final FermetureRepository fermetureRepository;

    public SiteService(SiteRepository siteRepository,
                       FermetureRepository fermetureRepository) {
        this.siteRepository = siteRepository;
        this.fermetureRepository = fermetureRepository;
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

    public List<LocalTime> genererCreneaux(Long siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site introuvable"));

        List<LocalTime> creneaux = new ArrayList<>();
        LocalTime heure = site.getHeureOuverture();
        int dureeSlot = be.ephec.backend.model.Constantes.DUREE_MATCH
                + be.ephec.backend.model.Constantes.PAUSE_MATCH;

        while (heure.plusMinutes(be.ephec.backend.model.Constantes.DUREE_MATCH)
                .isBefore(site.getHeureFermeture().plusSeconds(1))) {
            creneaux.add(heure);
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