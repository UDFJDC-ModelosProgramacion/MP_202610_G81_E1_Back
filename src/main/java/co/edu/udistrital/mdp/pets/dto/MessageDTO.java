package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class MessageDTO extends BaseDTO {
    private String content;
    private Date timestamp;
    private Boolean isRead;
}
