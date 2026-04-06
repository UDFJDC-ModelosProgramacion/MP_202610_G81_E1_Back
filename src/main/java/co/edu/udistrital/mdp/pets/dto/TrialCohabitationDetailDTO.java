package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for TrialCohabitation, including nested pet and adoption objects.
 * Used in GET by ID responses.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TrialCohabitationDetailDTO extends TrialCohabitationDTO {

    /** Full pet information (nested object). */
    private PetDTO pet;

    /** Linked adoption details (nested object). */
    private AdoptionDTO adoption;
}
