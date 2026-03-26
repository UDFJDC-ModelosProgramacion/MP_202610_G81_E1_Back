package co.edu.udistrital.mdp.pets.dtos;

import co.edu.udistrital.mdp.pets.enums.PetStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Detailed DTO for Pet including all attributes and relationships.
 * Used for detailed view and creation/update processes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {

    // Relationships mapped as DTOs to avoid recursion and expose only needed data
    private MedicalHistoryDTO medicalHistory;
    private List<ReviewDTO> reviews;
    private List<AdoptionDTO> adoptions;
    private List<AdoptionRequestDTO> adoptionRequests;
    private List<AdoptionFollowUpDTO> followUps;
    private List<TrialCohabitationDTO> trials;
    
    // We usually only include the basic info of the shelter to avoid a massive JSON
    private ShelterDTO shelter; 
}
