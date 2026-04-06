package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportStrategyDTO extends BaseDTO {
    private String type; // e.g., "ADOPTION", "MEDICAL", "RETURN"
}
