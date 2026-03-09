package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;


@Entity
@DiscriminatorValue("MEDICAL")
public class MedicalClearanceStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Implementation for medical clearance check
        return true; 
    }
}
