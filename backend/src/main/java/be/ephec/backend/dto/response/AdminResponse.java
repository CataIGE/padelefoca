package be.ephec.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String typeAdmin;
    private Long siteId;
}