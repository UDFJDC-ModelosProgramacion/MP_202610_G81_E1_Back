package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("SMS")
public class SMSNotificationStrategyEntity extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Lógica de envío SMS
    }
}
