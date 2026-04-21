package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccineDTO extends BaseDTO{
    private String name;
    private Integer validityMonths;
    private String description;
}
