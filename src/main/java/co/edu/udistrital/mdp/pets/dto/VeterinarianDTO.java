package co.edu.udistrital.mdp.pets.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Basic DTO for Veterinarian list views.
 * Inherits identity fields from UserDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VeterinarianDTO extends UserDTO {
    
    private String specialty;
    private String availability;
	private ShelterDTO shelter;
}
