package be.ephec.backend.service;

import be.ephec.backend.dto.request.InscriptionJoueurRequest;
import be.ephec.backend.dto.response.JoueurResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.MembreLibre;
import be.ephec.backend.model.MembreSite;
import be.ephec.backend.model.MembreGlobal;
import be.ephec.backend.model.Joueur;
import be.ephec.backend.model.enums.TypeMembre;
import be.ephec.backend.repository.JoueurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoueurService {

    private final JoueurRepository joueurRepository;

    public JoueurService(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    public JoueurResponse inscrire(InscriptionJoueurRequest request) {
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

        if (joueur.getNombreReservationsSansPenalite() >= 6
                && joueur.getTypeMembre() != TypeMembre.GLOBAL) {

            String nouveauMatricule = "G" + matricule.substring(1);
            joueurRepository.updateDtype(matricule);
            joueur.setTypeMembre(TypeMembre.GLOBAL);
            joueur.setMatricule(nouveauMatricule);
            joueurRepository.save(joueur);
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