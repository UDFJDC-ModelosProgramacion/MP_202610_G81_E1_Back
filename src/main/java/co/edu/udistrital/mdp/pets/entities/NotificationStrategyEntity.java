package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class NotificationStrategyEntity extends BaseEntity {
    public abstract void send(NotificationEntity notification);
}
