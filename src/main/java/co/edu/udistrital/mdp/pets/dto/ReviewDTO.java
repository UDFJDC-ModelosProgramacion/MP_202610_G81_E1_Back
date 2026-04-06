package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReviewDTO extends BaseDTO {
    private Integer rating;
    private String comment;
    private LocalDate date;
}
