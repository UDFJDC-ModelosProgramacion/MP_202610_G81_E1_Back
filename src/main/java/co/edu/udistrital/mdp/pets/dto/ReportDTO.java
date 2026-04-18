package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReportDTO extends BaseDTO {
    private Long id;
    private String reason;
    private String status;
    private LocalDate generateDate;
    private Long shelterId;
    private Long strategyId;
    private Long reportedUserId;
}
