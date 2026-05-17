package be.ephec.backend.repository;

import be.ephec.backend.model.Fermeture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FermetureRepository extends JpaRepository<Fermeture, Long> {

    @Query("SELECT f FROM Fermeture f WHERE :date BETWEEN f.dateDebut AND f.dateFin AND (f.site IS NULL OR f.site.id = :siteId)")
    List<Fermeture> findFermeturesActives(@Param("date") LocalDate date, @Param("siteId") Long siteId);
}