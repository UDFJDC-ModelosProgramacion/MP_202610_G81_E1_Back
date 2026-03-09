package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class NotificationStrategyEntity extends BaseEntity {
    public abstract void send(NotificationEntity notification);
}
