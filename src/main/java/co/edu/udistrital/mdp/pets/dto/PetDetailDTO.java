package co.edu.udistrital.mdp.pets.dto;

import co.edu.udistrital.mdp.pets.enums.PetStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.ArrayList;
/**
 * Detailed DTO for Pet including all attributes and relationships.
 * Used for detailed view and creation/update processes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {

    private List<ReviewDTO> reviews = new ArrayList<>();
    private List<AdoptionDTO> adoptions = new ArrayList<>();
    private List<AdoptionRequestDTO> adoptionRequests = new ArrayList<>();
    private List<AdoptionFollowUpDTO> followUps = new ArrayList<>();
    private List<TrialCohabitationDTO> trials = new ArrayList<>();
}
