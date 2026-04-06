package co.edu.udistrital.mdp.pets.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdoptionRequestDetailDTO extends AdoptionRequestDTO {
    
    private TrialCohabitationDTO trialCohabitation;
}
