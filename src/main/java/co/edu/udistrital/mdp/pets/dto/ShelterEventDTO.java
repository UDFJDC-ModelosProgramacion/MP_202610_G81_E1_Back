package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;

@Data
@NoArgsConstructor
public class ShelterEventDTO extends BaseDTO {
    private String title;
    private String description;
    private String location;
    private ProcessStatus status;
    private LocalDate date;
}
