package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MedicalEventDTO {
    private Long id;
    private LocalDate eventDate;
    private String description;
}