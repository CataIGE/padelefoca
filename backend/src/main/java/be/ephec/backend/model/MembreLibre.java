package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("LIBRE")
@NoArgsConstructor
@Getter
@Setter
public class MembreLibre extends Joueur {

    @Override
    public int getDelaiReservation() {
        return 5;
    }
}