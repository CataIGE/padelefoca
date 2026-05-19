package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreneauResponse {

    private String heure;
    private String statut; // LIBRE, MATCH_PUBLIC, MATCH_PRIVE, COMPLET
    private Long matchId;
    private int placesDisponibles;
}