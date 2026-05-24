package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueSiteResponse {

    private Long siteId;
    private String nomSite;
    private double chiffreAffaires;
    private double tauxRemplissage;
    private int nombreMatches;
    private int nombreJoueurs;
    private int nombreReservationsAnnee;
}