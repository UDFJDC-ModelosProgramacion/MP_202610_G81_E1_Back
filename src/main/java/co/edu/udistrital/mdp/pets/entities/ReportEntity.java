package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * Entity representing a system Report using Strategy Pattern context.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReportEntity extends BaseEntity {

    @Temporal(TemporalType.DATE)
    private Date generatedDate;

    /**
     * Name or type of the strategy used (e.g., "PDF", "EXCEL").
     */
    private String reportStrategy;

    /**
     * Logic for generating the report is handled in the Service layer.
     */
    public void generate() {
        // Triggered via Service implementation
    }
}
