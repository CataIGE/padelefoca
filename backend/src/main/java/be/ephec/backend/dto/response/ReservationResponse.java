package be.ephec.backend.dto.response;

import be.ephec.backend.model.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private String joueurMatricule;
    private String joueurNom;
    private Long matchId;
    private String siteNom;
    private String terrainNom;
    private LocalDateTime dateHeureMatch;
    private StatutReservation statutReservation;
    private double montantDu;
}