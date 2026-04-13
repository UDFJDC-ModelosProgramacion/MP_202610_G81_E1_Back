package co.edu.udistrital.mdp.pets.dto;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdoptionRequestDetailDTO extends AdoptionRequestDTO {
    
    private TrialCohabitationDTO trialCohabitation;
}
