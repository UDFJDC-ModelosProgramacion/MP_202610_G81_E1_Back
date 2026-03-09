package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base abstract entity for all report strategies.
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public abstract class ReportStrategyEntity extends BaseEntity {
    
    // Common metadata for strategies could go here
    
    public abstract void generate(ReportEntity report);
}
