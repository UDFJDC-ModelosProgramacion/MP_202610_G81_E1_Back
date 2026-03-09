package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing an Adoption Request.
 * Integrates with ApprovalStrategy for evaluation and links to TrialCohabitation.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdoptionRequestEntity extends BaseEntity {

    private LocalDate requestDate;
    private String status;

    // Relation: 1 Adopter has many AdoptionRequests
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private AdopterEntity adopter;

    // Relation: 1 Pet has many AdoptionRequests
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    // Relation: 1 AdoptionRequest can have 0 or 1 TrialCohabitation
    @PodamExclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "trial_cohabitation_id")
    private TrialCohabitationEntity trialCohabitation;

    // Relation: Aggregation with 1 ApprovalStrategy
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "strategy_id")
    private ApprovalStrategyEntity approvalStrategy;

    @PrePersist
    protected void onCreate() {
        if (this.requestDate == null) {
            this.requestDate = LocalDate.now();
        }
    }

    public void setStrategy(ApprovalStrategyEntity approvalStrategy) {
        this.approvalStrategy = approvalStrategy;
    }

    /**
     * Delegates evaluation logic to the assigned strategy.
     * @return boolean evaluation result.
     */
    public boolean evaluate() {
        if (approvalStrategy != null) {
            return approvalStrategy.evaluate(this);
        }
        return false;
    }
}
