package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("IN_APP")
public class InAppNotificationStrategy extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Lógica de envío de email (o vacía por ahora)
    }
}
