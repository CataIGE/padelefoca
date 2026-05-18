package be.ephec.backend.dto.request;

import be.ephec.backend.model.enums.TypeMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreerMatchRequest {

    private Long siteId;
    private LocalDateTime dateHeure;
    private TypeMatch typeMatch;
}