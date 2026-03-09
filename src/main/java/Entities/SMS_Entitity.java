package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SMSEntity extends NotificationEntity {

    private String phoneNumber;
    private String smsContent;  // sentNotification del diagrama
}
