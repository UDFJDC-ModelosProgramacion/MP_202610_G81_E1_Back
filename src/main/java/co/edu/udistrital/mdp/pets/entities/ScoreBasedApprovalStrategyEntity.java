package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("SCORE")
public class ScoreBasedApprovalStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
			// Lógica: Comparar filtros de adoptante vs necesidades de la mascota
        return true; 
    }
}
