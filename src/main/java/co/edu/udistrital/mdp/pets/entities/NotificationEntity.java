package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class NotificationEntity extends BaseEntity {

    private String message;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    private Boolean isRead;
	
	@PodamExclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "strategy_id")
    private NotificationStrategyEntity notificationStrategy;
	
	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "user_id") // Se relaciona con User 
    private UserEntity user;
}
