package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MANUAL")
public class ManualApprovalStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Lógica: Requiere validación por un administrador
		return true; 
    }
}
