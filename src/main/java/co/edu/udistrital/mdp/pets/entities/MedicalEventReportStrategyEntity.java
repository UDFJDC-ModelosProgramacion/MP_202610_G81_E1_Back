package co.edu.udistrital.mdp.pets.entities;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("MEDICAL")
public class MedicalEventReportStrategyEntity extends ReportStrategyEntity {
    @Override
    public void generate(ReportEntity report) {
        // Implementar lógica 
    }
}
