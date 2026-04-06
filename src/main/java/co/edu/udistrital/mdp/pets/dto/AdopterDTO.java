package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for Adopter list views.
 * Inherits identity fields from UserDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdopterDTO extends UserDTO {

    private String housingType;
    private Boolean hasChildren;
    private Boolean hasOtherPets;
}
