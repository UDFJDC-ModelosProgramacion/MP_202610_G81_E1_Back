package co.edu.udistrital.mdp.pets.dto;

import co.edu.udistrital.mdp.pets.enums.PetStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for list representations of a Pet.
 * Focuses on performance and essential information.
 */
@Data
@NoArgsConstructor
public class PetDTO extends BaseDTO {
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String sex;
    private String size;
    private String photos;
    private PetStatus status;	
    private String temperament;
    private String specialNeeds;
    private String description;
    private String activityLevel;
    private String origin;
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private String spaceRequired;
	private ShelterDTO shelter;
	private MedicalHistoryDTO medicalHistory;
}
