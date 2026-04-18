package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalHistoryDTO {
    private Long id;
    private LocalDate lastCheckup;
    private PetDTO pet;
    private String description;
    private String notes;
}

