package be.ephec.backend.dto.response;

import be.ephec.backend.model.enums.StatutMatch;
import be.ephec.backend.model.enums.TypeMatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

    private Long id;
    private Long siteId;
    private String siteNom;
    private Long terrainId;
    private String terrainNom;
    private String organisateurMatricule;
    private String organisateurNom;
    private LocalDateTime dateHeure;
    private TypeMatch typeMatch;
    private StatutMatch statutMatch;
    private int nombreJoueurs;
    private int placesDisponibles;
    private List<String> joueursMatricules;
}