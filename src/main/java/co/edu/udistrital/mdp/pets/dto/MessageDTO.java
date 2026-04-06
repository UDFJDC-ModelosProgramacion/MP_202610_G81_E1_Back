package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import java.util.Date;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDTO extends BaseDTO {
    private String content;
    private Date timestamp;
    private Boolean isRead;
}
