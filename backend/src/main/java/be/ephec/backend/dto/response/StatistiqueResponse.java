package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueResponse {

    private double chiffreAffaires;
    private double tauxRemplissage;
    private int nombreMatchs;
    private Map<String, Long> nombreMembresByType;
}