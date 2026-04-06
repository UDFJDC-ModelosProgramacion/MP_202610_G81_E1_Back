package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalHistoryDetailDTO extends MedicalHistoryDTO {
    private String bloodType;
    private Boolean isSterilized;
    private String arrivalCondition;
    private PetDTO pet;
    private List<MedicalEventDTO> medicalEvents = new ArrayList<>();
    private List<VaccinationRecordDTO> vaccinations = new ArrayList<>();
}