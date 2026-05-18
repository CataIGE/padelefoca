package be.ephec.backend.dto.response;

import be.ephec.backend.model.enums.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {

    private Long id;
    private String joueurMatricule;
    private String joueurNom;
    private Long reservationId;
    private double montant;
    private StatutPaiement statutPaiement;
    private LocalDateTime datePaiement;
}