package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for Adopter entity.
 * Extends UserDTO to inherit common user fields (name, email, phone, password).
 * Used for both create and update operations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdopterDTO extends UserDTO {

    /** Type of housing where the adopter lives (e.g., "HOUSE", "APARTMENT"). */
    private String housingType;

    /** Indicates whether the adopter has children living at home. */
    private Boolean hasChildren;

    /** Indicates whether the adopter has other pets at home. */
    private Boolean hasOtherPets;
}
