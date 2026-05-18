package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteResponse {

    private Long id;
    private String nom;
    private String adresse;
    private LocalTime heureOuverture;
    private LocalTime heureFermeture;
    private Set<DayOfWeek> joursRepos;
}