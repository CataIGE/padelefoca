package be.ephec.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FermetureRequest {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private Long siteId;
}