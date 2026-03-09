package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("MEDICAL")
public class MedicalClearanceStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Implementation for medical clearance check
        return true; 
    }
}
