package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Detailed DTO for Adoption, including nested objects.
 * Used in GET by ID responses to expose the adopter, pet, trial cohabitation,
 * and follow-up records associated with the adoption.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdoptionDetailDTO extends AdoptionDTO {

    /** Full adopter information (nested object). */
    private AdopterDTO adopter;

    /** Full pet information (nested object). */
    private PetDTO pet;

    /** Trial cohabitation associated with this adoption, if any. */
    private TrialCohabitationDTO trialCohabitation;

    /** List of follow-up records for this adoption. */
    private List<AdoptionFollowUpDTO> followUps;
}
