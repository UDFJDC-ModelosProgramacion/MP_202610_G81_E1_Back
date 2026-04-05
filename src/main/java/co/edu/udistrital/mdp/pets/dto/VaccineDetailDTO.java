package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccineDetailDTO extends VaccineDTO {
    private Integer validityMonths;
    private String description;
}