package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueGlobaleResponse {

    private double chiffreAffairesGlobal;
    private double tauxRemplissageGlobal;
    private int nombreMatchesGlobal;
    private int nombreJoueursGlobal;
    private int nombreReservationsAnneeGlobal;
    private RepartitionMembres repartitionMembres;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepartitionMembres {
        private long libres;
        private long site;
        private long globaux;
    }
}