package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalEventDetailDTO extends MedicalEventDTO {
    private String notes;
    private MedicalHistoryDTO medicalHistory;
}