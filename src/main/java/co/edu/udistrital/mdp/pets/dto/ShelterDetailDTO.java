package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Detailed DTO for Shelter, including management lists and active pets.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShelterDetailDTO extends ShelterDTO {
   
    
    // We skip reports and messages here unless its an admin view
    // because they are usually handled in separate endpoints for security.
}
