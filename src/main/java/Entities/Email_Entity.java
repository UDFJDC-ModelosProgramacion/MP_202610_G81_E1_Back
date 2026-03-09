// EmailEntity.java
package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EmailEntity extends NotificationEntity {

    private String recipientEmail;
    private String subject;
    private String emailContent;  // sentNotification
}
