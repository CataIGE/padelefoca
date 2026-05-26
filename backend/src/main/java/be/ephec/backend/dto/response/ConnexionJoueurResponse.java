package be.ephec.backend.dto.response;

import be.ephec.backend.model.enums.TypeMembre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnexionJoueurResponse {

    private String matricule;
    private String nom;
    private String prenom;
    private TypeMembre typeMembre;
    private Long siteId;
}