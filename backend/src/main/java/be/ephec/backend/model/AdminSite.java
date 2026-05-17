package be.ephec.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("SITE")
@NoArgsConstructor
@Getter
@Setter
public class AdminSite extends Administrateur {

}