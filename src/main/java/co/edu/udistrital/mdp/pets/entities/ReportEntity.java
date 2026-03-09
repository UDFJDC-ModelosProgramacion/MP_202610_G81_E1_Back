package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReportEntity extends BaseEntity {

    private LocalDate generateDate;

    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;

    @Transient
    private ReportStrategyEntity reportStrategy;

    public void setStrategy(ReportStrategyEntity reportStrategy) {
        this.reportStrategy = reportStrategy;
    }

    public void generate() {
        if (reportStrategy != null) {
            reportStrategy.generate(this);
        }
    }
}
