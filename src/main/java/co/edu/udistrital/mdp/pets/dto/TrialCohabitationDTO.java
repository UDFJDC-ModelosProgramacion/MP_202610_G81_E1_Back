package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for TrialCohabitation entity.
 * Represents a trial period where a pet lives with a potential adopter
 * before the adoption is finalized.
 *
 * Allowed values for result: "EN_PROCESO", "EXITOSA", "FALLIDA", "CANCELADA"
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TrialCohabitationDTO extends BaseDTO {

    /** Start date of the trial cohabitation period. Required. */
    private LocalDate startDate;

    /** End date of the trial cohabitation period. Must be after startDate. Required. */
    private LocalDate endDate;

    /**
     * Result of the trial cohabitation.
     * Allowed values: "EN_PROCESO", "EXITOSA", "FALLIDA", "CANCELADA"
     */
    private String result;

    /** Current status of the trial cohabitation process. */
    private String status;

    /** ID of the pet involved in this trial. */
    private Long petId;

    /** ID of the adoption linked to this trial cohabitation. */
    private Long adoptionId;
}
