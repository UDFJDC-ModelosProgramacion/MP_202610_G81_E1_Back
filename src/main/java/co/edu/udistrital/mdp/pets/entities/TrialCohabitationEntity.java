package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class TrialCohabitationEntity extends BaseEntity {

    private LocalDate startDate;
    private LocalDate endDate;
    private String result;
	
	@PodamExclude
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    @PodamExclude
    @OneToOne
    private AdoptionEntity adoption;
}
