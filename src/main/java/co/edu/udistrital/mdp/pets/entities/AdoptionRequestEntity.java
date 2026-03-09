package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdoptionRequestEntity extends BaseEntity {

    private LocalDate requestDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    @OneToOne
    @JoinColumn(name = "trial_cohabitation_id")
    private TrialCohabitationEntity trialCohabitation;

    @Transient
    private ApprovalStrategy approvalStrategy;

    public void setStrategy(ApprovalStrategy approvalStrategy) {
        this.approvalStrategy = approvalStrategy;
    }

    public boolean evaluate() {
        if (approvalStrategy != null) {
            return approvalStrategy.evaluate(this);
        }
        return false;
    }
}
