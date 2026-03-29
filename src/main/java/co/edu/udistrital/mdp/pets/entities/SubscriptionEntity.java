package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class SubscriptionEntity extends BaseEntity {

    private Boolean active = true;
    
	@ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
