package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for Veterinarian list views.
 * Inherits identity fields from UserDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VeterinarianDTO extends UserDTO {
    
    private String specialty;
    private String availability;
	private ShelterDTO shelter;
}
