package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MEDICAL")
public class MedicalEventReportStrategyEntity extends ReportStrategyEntity {

    @Override
    public void generate(ReportEntity report) {
        // Implementar lógica específica para reportes médicos
    }
}
