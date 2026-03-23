package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("RETURN")
public class ReturnReportStrategyEntity extends ReportStrategyEntity {

    @Override
    public void generate(ReportEntity report) {
        // TODO: Implementar lógica específica para devoluciones
        // Ejemplo: Cambiar el estado de la mascota a AVAILABLE nuevamente
    }
}
