package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("GLOBAL")
@NoArgsConstructor
@Getter
@Setter
public class MembreGlobal extends Joueur {

    @Override
    public int getDelaiReservation() {
        return 21;
    }
}