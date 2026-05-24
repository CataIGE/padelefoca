package be.ephec.backend.service;

import be.ephec.backend.dto.request.CreerAdminRequest;
import be.ephec.backend.dto.request.ModifierAdminRequest;
import be.ephec.backend.dto.response.AdminResponse;
import be.ephec.backend.exception.BadRequestException;
import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.AdminGlobal;
import be.ephec.backend.model.AdminSite;
import be.ephec.backend.model.Administrateur;
import be.ephec.backend.model.Site;
import be.ephec.backend.model.enums.TypeAdmin;
import be.ephec.backend.repository.AdministrateurRepository;
import be.ephec.backend.repository.SiteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestionAdminService {

    private final AdministrateurRepository administrateurRepository;
    private final SiteRepository siteRepository;
    private final PasswordEncoder passwordEncoder;

    public GestionAdminService(AdministrateurRepository administrateurRepository,
                               SiteRepository siteRepository,
                               PasswordEncoder passwordEncoder) {
        this.administrateurRepository = administrateurRepository;
        this.siteRepository = siteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminResponse> getTousLesAdmins() {
        return administrateurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminResponse creerAdmin(CreerAdminRequest request) {
        if (administrateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un admin avec cet email existe déjà");
        }

        Administrateur admin;
        if ("GLOBAL".equals(request.getTypeAdmin())) {
            admin = new AdminGlobal();
            admin.setTypeAdmin(TypeAdmin.GLOBAL);
        } else {
            if (request.getSiteId() == null) {
                throw new BadRequestException("Un admin de site doit avoir un site");
            }
            Site site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new NotFoundException("Site introuvable"));
            admin = new AdminSite();
            admin.setTypeAdmin(TypeAdmin.SITE);
            admin.setSite(site);
        }

        admin.setNom(request.getNom());
        admin.setPrenom(request.getPrenom());
        admin.setEmail(request.getEmail());
        admin.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));

        return toResponse(administrateurRepository.save(admin));
    }

    public AdminResponse modifierAdmin(Long adminId, ModifierAdminRequest request) {
        Administrateur admin = administrateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));

        admin.setNom(request.getNom());
        admin.setPrenom(request.getPrenom());
        admin.setEmail(request.getEmail());

        if ("SITE".equals(request.getTypeAdmin()) && request.getSiteId() != null) {
            Site site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new NotFoundException("Site introuvable"));
            admin.setSite(site);
        }

        return toResponse(administrateurRepository.save(admin));
    }

    public void supprimerAdmin(Long adminId) {
        Administrateur admin = administrateurRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException("Admin introuvable"));

        long nombreGlobaux = administrateurRepository.findAll()
                .stream()
                .filter(a -> a.getTypeAdmin() == TypeAdmin.GLOBAL)
                .count();

        if (admin.getTypeAdmin() == TypeAdmin.GLOBAL && nombreGlobaux <= 1) {
            throw new BadRequestException("Impossible de supprimer le dernier admin global");
        }

        administrateurRepository.deleteById(adminId);
    }

    public AdminResponse toResponse(Administrateur admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getNom(),
                admin.getPrenom(),
                admin.getEmail(),
                admin.getTypeAdmin().name(),
                admin.getSite() != null ? admin.getSite().getId() : null
        );
    }
}