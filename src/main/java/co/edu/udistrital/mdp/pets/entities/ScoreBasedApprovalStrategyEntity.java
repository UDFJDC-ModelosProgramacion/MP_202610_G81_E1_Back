package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;


@Entity
@DiscriminatorValue("SCORE")
public class ScoreBasedApprovalStrategyEntity extends ApprovalStrategyEntity {
    @Override
    public boolean evaluate(AdoptionRequestEntity request) {
        // Logic based on adopter score
        return true;
    }
}
