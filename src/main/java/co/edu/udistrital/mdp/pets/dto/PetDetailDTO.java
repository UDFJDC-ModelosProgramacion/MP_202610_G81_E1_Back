package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for Pet including all attributes and relationships.
 * Used for detailed view and creation/update processes.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PetDetailDTO extends PetDTO {
}
