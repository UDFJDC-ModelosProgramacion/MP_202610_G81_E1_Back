package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Date;

/**
 * Context entity for the Strategy Pattern.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReportEntity extends BaseEntity {

    @Temporal(TemporalType.DATE)
    private Date generatedDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "strategy_id")
    private ReportStrategyEntity reportStrategy;

    public void generate() {
        // Logic handled by strategy implementation
    }
}
