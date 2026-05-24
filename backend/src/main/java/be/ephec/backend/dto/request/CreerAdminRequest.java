package be.ephec.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreerAdminRequest {

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String typeAdmin;
    private Long siteId;
}