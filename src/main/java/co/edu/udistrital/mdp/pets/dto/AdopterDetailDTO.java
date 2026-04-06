package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Detailed DTO for Adopter, including related collections.
 * Used in GET by ID responses to expose associated adoptions and requests.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AdopterDetailDTO extends AdopterDTO {

    /** List of adoptions associated with this adopter. */
    private List<AdoptionDTO> adoptions;

    /** List of adoption requests submitted by this adopter. */
    private List<AdoptionRequestDTO> adoptionRequests;


}
