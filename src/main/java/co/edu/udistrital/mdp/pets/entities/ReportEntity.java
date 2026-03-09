package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.jemos.podam.common.PodamExclude;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReportEntity extends BaseEntity {

    private LocalDate generateDate;

	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "shelter_id")
	@EqualsAndHashCode.Exclude // Prevents infinite recursion in tests
    private ShelterEntity shelter;

	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "strategy_id")
	@EqualsAndHashCode.Exclude
    private ReportStrategyEntity reportStrategy;
	
}
