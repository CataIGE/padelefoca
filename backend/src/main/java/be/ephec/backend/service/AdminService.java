package be.ephec.backend.service;

import be.ephec.backend.dto.request.FermetureRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.dto.response.MatchResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.TypeAdmin;
import be.ephec.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final JoueurRepository joueurRepository;
    private final MatchRepository matchRepository;
    private final FermetureRepository fermetureRepository;
    private final SiteRepository siteRepository;
    private final JoueurService joueurService;
    private final MatchService matchService;

    public AdminService(JoueurRepository joueurRepository,
                        MatchRepository matchRepository,
                        FermetureRepository fermetureRepository,
                        SiteRepository siteRepository,
                        JoueurService joueurService,
                        MatchService matchService) {
        this.joueurRepository = joueurRepository;
        this.matchRepository = matchRepository;
        this.fermetureRepository = fermetureRepository;
        this.siteRepository = siteRepository;
        this.joueurService = joueurService;
        this.matchService = matchService;
    }

    public List<JoueurResponse> getJoueurs(Administrateur admin) {
        if (admin.getTypeAdmin() == TypeAdmin.GLOBAL) {
            return joueurRepository.findAll()
                    .stream()
                    .map(joueurService::toResponse)
                    .toList();
        } else {
            Long siteId = admin.getSite().getId();
            return joueurRepository.findAll()
                    .stream()
                    .filter(j -> j.getSite() != null && j.getSite().getId().equals(siteId))
                    .map(joueurService::toResponse)
                    .toList();
        }
    }

    public List<MatchResponse> getMatchs(Administrateur admin) {
        if (admin.getTypeAdmin() == TypeAdmin.GLOBAL) {
            return matchRepository.findAll()
                    .stream()
                    .map(matchService::toResponse)
                    .toList();
        } else {
            return matchService.getMatchsBySite(admin.getSite().getId());
        }
    }

    public void ajouterFermeture(FermetureRequest request) {
        Fermeture fermeture = new Fermeture();
        fermeture.setDateDebut(request.getDateDebut());
        fermeture.setDateFin(request.getDateFin());
        fermeture.setMotif(request.getMotif());

        if (request.getSiteId() != null) {
            Site site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new NotFoundException("Site introuvable"));
            fermeture.setSite(site);
        }

        fermetureRepository.save(fermeture);
    }

    public void supprimerFermeture(Long fermetureId) {
        if (!fermetureRepository.existsById(fermetureId)) {
            throw new NotFoundException("Fermeture introuvable");
        }
        fermetureRepository.deleteById(fermetureId);
    }
}