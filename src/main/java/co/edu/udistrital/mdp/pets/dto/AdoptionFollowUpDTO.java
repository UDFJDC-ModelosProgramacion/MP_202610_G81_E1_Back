package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * llenar :) 
 */
@Data
@NoArgsConstructor
public class AdoptionFollowUpDTO extends BaseDTO {
	private String frequency;
    private String notes;
    private LocalDate followUpDate;
	private VeterinarianDTO veterinarian;
    private AdoptionDTO adoption;
    private PetDTO pet;
}

