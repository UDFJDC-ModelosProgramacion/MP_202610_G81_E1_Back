package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Context entity for the Strategy Pattern.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class MedicalEventReportStrategy extends ReportStrategyEntity {
    @Override
    public void generate(ReportEntity report) { /* Logic */ }
}
