package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MEDICAL")
public class MedicalClearanceStrategyEntity extends  ApprovalStrategyEntity{
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Revisar estado de salud/vacunas de la mascota
        return true; 
    }
}
