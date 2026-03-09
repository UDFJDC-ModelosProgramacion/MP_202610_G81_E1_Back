package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;


@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("EMAIL")
public class EmailNotificationStrategyEntity extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Logic to send email
    }
}
