package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
@EqualsAndHashCode(callSuper = true)
public class NotificationEntity extends BaseEntity {

    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "notification_strategy_id")
    private NotificationStrategyEntity notificationStrategy;

    // Lógica en Service
    public void setStrategy(NotificationStrategyEntity strategy) { /* */ }
    public void send() { /* usa strategy */ }
    public void markAsRead() { /* */ }
}
