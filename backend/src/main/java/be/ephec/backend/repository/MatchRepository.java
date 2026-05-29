package be.ephec.backend.repository;

import be.ephec.backend.model.Match;
import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTerrainSiteId(Long siteId);

    List<Match> findByTypeMatchAndStatutMatch(TypeMatch typeMatch, StatutMatch statutMatch);

    List<Match> findByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);

    List<Match> findByOrganisateurId(Long organisateurId);

    List<Match> findByDateHeureBefore(LocalDateTime dateHeure);
}