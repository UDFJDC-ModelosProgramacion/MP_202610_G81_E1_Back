package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ADOPTION")
public class AdoptionReportStrategyEntity extends ReportStrategyEntity {

    @Override
    public void generate(ReportEntity report) {
        // Implementar lógica específica para reportes de adopción
    }
}
