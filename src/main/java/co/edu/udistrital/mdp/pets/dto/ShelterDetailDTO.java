package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Detailed DTO for Shelter, including management lists and active pets.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShelterDetailDTO extends ShelterDTO {
    
    private List<PetDTO> pets;
    private List<VeterinarianDTO> veterinarians;
    private List<ShelterEventDTO> events;
    
    // We skip reports and messages here unless its an admin view
    // because they are usually handled in separate endpoints for security.
}
