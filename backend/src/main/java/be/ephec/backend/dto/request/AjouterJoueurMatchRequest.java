package be.ephec.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AjouterJoueurMatchRequest {

    private List<String> matricules;
}