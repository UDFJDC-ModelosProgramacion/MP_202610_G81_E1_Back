package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("MANUAL")
public class ManualApprovalStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Logic for manual staff review
        return false;
    }
}
