package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccinationRecordDTO extends BaseDTO{
    private LocalDate applicationDate;
    private LocalDate nextDueDate;
    private LocalDate vaccinationDate;
    private VaccineDTO vaccine;
    private PetDTO pet;
    private String notes;
    private MedicalHistoryDTO medicalHistory;
	private VeterinarianDTO veterinarian;

}
