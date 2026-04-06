package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AdoptionRequestDTO extends BaseDTO{
    private LocalDate requestDate;
    private String status;

	private Long petId; 
    private Long adopterId;
    private Long strategyId;
}
