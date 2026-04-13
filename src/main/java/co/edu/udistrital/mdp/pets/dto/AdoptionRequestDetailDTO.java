package co.edu.udistrital.mdp.pets.dto;

import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class AdoptionRequestDetailDTO extends AdoptionRequestDTO {
    
    private TrialCohabitationDTO trialCohabitation;
}
