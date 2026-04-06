package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class NotificationDTO extends BaseDTO {
    private String message;
    private Date date;
    private Boolean isRead;
	private Long userId;
	private NotificationStrategyDTO notificationStrategy;
}
