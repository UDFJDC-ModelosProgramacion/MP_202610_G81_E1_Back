package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Detailed DTO for Adopter, extending AdopterDTO.
 * Following the same pattern as VeterinarianDetailDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdopterDetailDTO extends AdopterDTO {
}
