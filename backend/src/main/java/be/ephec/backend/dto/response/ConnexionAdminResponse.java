package be.ephec.backend.dto.response;

import be.ephec.backend.model.enums.TypeAdmin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnexionAdminResponse {

    private String token;
    private String nom;
    private String prenom;
    private TypeAdmin typeAdmin;
    private Long siteId;
}