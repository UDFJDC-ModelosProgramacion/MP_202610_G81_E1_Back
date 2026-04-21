package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ShelterEventDTO extends BaseDTO {
    private String title;
    private String description;
    private String location;
    private String status;
    private LocalDate date;
	private String shelter;
}
