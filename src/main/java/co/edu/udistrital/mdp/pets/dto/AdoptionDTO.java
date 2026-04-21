package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Adoption entity.
 * Contains the essential fields for creating or listing adoption records.
 * Adopter and Pet are referenced by their IDs to avoid circular serialization.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdoptionDTO extends BaseDTO {

    /** Date when the adoption was registered. Immutable once set. */
    private LocalDate adoptionDate;

    /** Current status of the adoption process. */
    private String status;

    /** ID of the adopter involved in this adoption. */
    private Long adopterId;

    /** ID of the pet being adopted. */
    private Long petId;
}
