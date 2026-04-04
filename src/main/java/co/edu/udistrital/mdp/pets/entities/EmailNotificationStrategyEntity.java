package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("EMAIL")
@NoArgsConstructor
public class EmailNotificationStrategyEntity extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Lógica de envío de email (o vacía por ahora)
    }
}
