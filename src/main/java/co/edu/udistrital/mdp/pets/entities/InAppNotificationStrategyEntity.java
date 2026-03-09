package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("IN_APP")
public class InAppNotificationStrategyEntity extends NotificationStrategyEntity {
    @Override
    public void send(NotificationEntity notification) {
        // Logic to send in app
    }
}
