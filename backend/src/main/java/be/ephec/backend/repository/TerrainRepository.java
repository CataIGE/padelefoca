package be.ephec.backend.repository;

import be.ephec.backend.model.Terrain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerrainRepository extends JpaRepository<Terrain, Long> {

    List<Terrain> findBySiteId(Long siteId);
}