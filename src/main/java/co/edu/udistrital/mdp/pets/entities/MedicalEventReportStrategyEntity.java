package co.edu.udistrital.mdp.pets.entities;

import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
