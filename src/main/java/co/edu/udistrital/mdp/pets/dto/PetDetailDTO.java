package co.edu.udistrital.mdp.pets.dto;

import co.edu.udistrital.mdp.pets.enums.PetStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;
/**
 * Detailed DTO for Pet including all attributes and relationships.
 * Used for detailed view and creation/update processes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PetDetailDTO extends PetDTO {
}
