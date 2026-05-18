package be.ephec.backend.service;

import be.ephec.backend.exception.NotFoundException;
import be.ephec.backend.model.Terrain;
import be.ephec.backend.repository.TerrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerrainService {

    private final TerrainRepository terrainRepository;

    public TerrainService(TerrainRepository terrainRepository) {
        this.terrainRepository = terrainRepository;
    }

    public List<Terrain> getTerrainsBySite(Long siteId) {
        return terrainRepository.findBySiteId(siteId);
    }

    public Terrain getTerrainDisponible(Long siteId, java.time.LocalDateTime dateHeure) {
        List<Terrain> terrains = terrainRepository.findBySiteId(siteId);
        if (terrains.isEmpty()) {
            throw new NotFoundException("Aucun terrain disponible sur ce site");
        }
        return terrains.get(0);
    }

    public Terrain getTerrainById(Long id) {
        return terrainRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Terrain avec id " + id + " introuvable"));
    }
}