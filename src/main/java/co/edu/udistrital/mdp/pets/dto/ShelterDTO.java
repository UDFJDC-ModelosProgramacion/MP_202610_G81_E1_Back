package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic DTO for Shelter list views.
 */
@Data
@NoArgsConstructor
public class ShelterDTO extends BaseDTO {
    private String name;
    private String city;
    private String email;
    private String gallery;
	private String description;
}
