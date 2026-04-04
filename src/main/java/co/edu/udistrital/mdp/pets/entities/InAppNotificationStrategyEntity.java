package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("IN_APP")
@NoArgsConstructor
public class InAppNotificationStrategyEntity extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Lógica de envío de email (o vacía por ahora)
    }
}
