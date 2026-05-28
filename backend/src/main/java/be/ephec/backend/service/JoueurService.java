package be.ephec.backend.service;

import be.ephec.backend.dto.request.InscriptionJoueurRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.MembreGlobal;
import be.ephec.backend.model.Joueur;
import be.ephec.backend.model.Reservation;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import be.ephec.backend.repository.ReservationRepository;
import be.ephec.backend.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoueurService {

    private final JoueurRepository joueurRepository;
    private final ReservationRepository reservationRepository;
    private final SiteRepository siteRepository;

    public JoueurService(JoueurRepository joueurRepository,
                         ReservationRepository reservationRepository,
                         SiteRepository siteRepository) {
        this.joueurRepository = joueurRepository;
        this.reservationRepository = reservationRepository;
        this.siteRepository = siteRepository;
    }

    public JoueurResponse inscrire(InscriptionJoueurRequest request) {
        if (joueurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte existe déjà avec cet email");
        }
        String matricule = genererMatricule("L");
        MembreLibre joueur = new MembreLibre();
        joueur.setMatricule(matricule);
        joueur.setNom(request.getNom());
        joueur.setPrenom(request.getPrenom());
        joueur.setEmail(request.getEmail());
        joueur.setTelephone(request.getTelephone());
        joueur.setTypeMembre(TypeMembre.LIBRE);
        joueurRepository.save(joueur);
        return toResponse(joueur);
    }

    public JoueurResponse getProfil(String matricule) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur avec matricule " + matricule + " introuvable"));
        return toResponse(joueur);
    }

    public List<JoueurResponse> getTousLesJoueurs() {
        return joueurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void verifierEtAppliquerPromotion(String matricule) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));

        int reservations = joueur.getNombreReservationsSansPenalite();

        // Promotion vers GLOBAL (6 réservations, joueur actuellement SITE)
        if (reservations >= 6 && joueur.getTypeMembre() == TypeMembre.SITE) {
            String nouveauMatricule = "G" + matricule.substring(1);
            System.out.println(">>> PROMOTION GLOBAL: " + matricule + " -> " + nouveauMatricule + " (reservations=" + reservations + ")");
            joueurRepository.updateDtype(matricule);
            joueur.setTypeMembre(TypeMembre.GLOBAL);
            joueur.setSite(null);
            joueurRepository.save(joueur);
            System.out.println(">>> SAVE OK, tentative updateMatricule");
            joueurRepository.updateMatricule(matricule, nouveauMatricule);
            System.out.println(">>> updateMatricule OK");
            return;
        }

        // Promotion vers SITE (3 réservations sur le même site, joueur LIBRE)
        if (reservations >= 3 && joueur.getTypeMembre() == TypeMembre.LIBRE) {
            List<Reservation> reservationsJoueur = reservationRepository.findByJoueurId(joueur.getId());

            java.util.Map<Long, Long> parSite = reservationsJoueur.stream()
                    .filter(r -> r.getStatutReservation() == be.ephec.backend.model.enums.StatutReservation.CONFIRMEE)
                    .collect(java.util.stream.Collectors.groupingBy(
                            r -> r.getMatch().getTerrain().getSite().getId(),
                            java.util.stream.Collectors.counting()
                    ));

            parSite.entrySet().stream()
                    .filter(e -> e.getValue() >= 3)
                    .findFirst()
                    .ifPresent(e -> {
                        be.ephec.backend.model.Site site = siteRepository.findById(e.getKey())
                                .orElseThrow(() -> new NotFoundException("Site introuvable"));
                        String nouveauMatricule = "S" + matricule.substring(1);
                        joueurRepository.updateDtypeToSite(matricule);
                        joueur.setTypeMembre(TypeMembre.SITE);
                        joueur.setSite(site);
                        joueurRepository.save(joueur);
                        joueurRepository.updateMatricule(matricule, nouveauMatricule);
                    });
        }
    }

    public void appliquerPenalite(String matricule) {
        Joueur joueur = joueurRepository.findByMatricule(matricule)
                .orElseThrow(() -> new NotFoundException("Joueur introuvable"));
        joueur.setPenaliteActive(true);
        joueur.setFinPenalite(java.time.LocalDate.now().plusWeeks(1));
        joueurRepository.save(joueur);
    }

    private String genererMatricule(String prefix) {
        String dernierMatricule = joueurRepository.findAll()
                .stream()
                .map(Joueur::getMatricule)
                .filter(m -> m.startsWith(prefix))
                .max(String::compareTo)
                .orElse(prefix + "0000");
        int numero = Integer.parseInt(dernierMatricule.substring(1)) + 1;
        return prefix + String.format("%04d", numero);
    }

    public JoueurResponse toResponse(Joueur joueur) {
        return new JoueurResponse(
                joueur.getId(),
                joueur.getMatricule(),
                joueur.getNom(),
                joueur.getPrenom(),
                joueur.getEmail(),
                joueur.getTelephone(),
                joueur.getTypeMembre(),
                joueur.isPenaliteActive(),
                joueur.getSoldeDu(),
                joueur.getSoldeCredit()
        );
    }
}