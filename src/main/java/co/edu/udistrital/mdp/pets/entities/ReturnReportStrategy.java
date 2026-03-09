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
public class ReturnReportStrategy extends ReportStrategyEntity {
    @Override
    public void generate(ReportEntity report) { /* Logic */ }
}
