package be.ephec.backend.config;

import be.ephec.backend.model.*;
import be.ephec.backend.model.enums.TypeAdmin;
import be.ephec.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SiteRepository siteRepository;
    private final TerrainRepository terrainRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SiteRepository siteRepository,
                           TerrainRepository terrainRepository,
                           AdministrateurRepository administrateurRepository,
                           PasswordEncoder passwordEncoder) {
        this.siteRepository = siteRepository;
        this.terrainRepository = terrainRepository;
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (siteRepository.count() == 0) {

            // BRUXELLES
            Site bruxelles = new Site();
            bruxelles.setNom("Bruxelles");
            bruxelles.setAdresse("Avenue du Padel 1, 1000 Bruxelles");
            bruxelles.setHeureOuverture(LocalTime.of(8, 0));
            bruxelles.setHeureFermeture(LocalTime.of(22, 0));
            bruxelles.setJoursRepos(Set.of(DayOfWeek.MONDAY));
            siteRepository.save(bruxelles);
            creerTerrain("Homer", bruxelles);
            creerTerrain("Lisa", bruxelles);
            creerTerrain("Bart", bruxelles);

            // GAND
            Site gand = new Site();
            gand.setNom("Gand");
            gand.setAdresse("Padelstraat 2, 9000 Gand");
            gand.setHeureOuverture(LocalTime.of(9, 0));
            gand.setHeureFermeture(LocalTime.of(21, 0));
            gand.setJoursRepos(Set.of(DayOfWeek.TUESDAY));
            siteRepository.save(gand);
            creerTerrain("Leonard", gand);
            creerTerrain("Sheldon", gand);
            creerTerrain("Howard", gand);

            // NAMUR
            Site namur = new Site();
            namur.setNom("Namur");
            namur.setAdresse("Rue du Padel 3, 5000 Namur");
            namur.setHeureOuverture(LocalTime.of(10, 0));
            namur.setHeureFermeture(LocalTime.of(23, 0));
            namur.setJoursRepos(Set.of(DayOfWeek.THURSDAY));
            siteRepository.save(namur);
            creerTerrain("Voldy", namur);
            creerTerrain("Dumby", namur);
            creerTerrain("Harry", namur);
        }

        if (administrateurRepository.count() == 0) {
            AdminGlobal admin = new AdminGlobal();
            admin.setNom("Admin");
            admin.setPrenom("Global");
            admin.setEmail("admin@padelefoca.be");
            admin.setMotDePasse(passwordEncoder.encode("Admin2024!"));
            admin.setTypeAdmin(TypeAdmin.GLOBAL);
            administrateurRepository.save(admin);
        }
    }

    private void creerTerrain(String nom, Site site) {
        Terrain terrain = new Terrain();
        terrain.setNom(nom);
        terrain.setSite(site);
        terrainRepository.save(terrain);
    }
}