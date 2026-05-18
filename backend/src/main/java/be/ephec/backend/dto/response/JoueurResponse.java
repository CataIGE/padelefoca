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
public class JoueurResponse {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private TypeMembre typeMembre;
    private boolean penaliteActive;
    private double soldeDu;
    private double soldeCredit;
}