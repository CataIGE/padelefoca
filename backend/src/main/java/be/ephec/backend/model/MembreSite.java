package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("SITE")
@NoArgsConstructor
@Getter
@Setter
public class MembreSite extends Joueur {

    @Override
    public int getDelaiReservation() {
        return 14;
    }
}