package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class AdoptionEntity extends BaseEntity {

    private LocalDate adoptionDate;

    @PodamExclude
    @ManyToOne
    private AdopterEntity adopter;

    @PodamExclude
    @ManyToOne
    private PetEntity pet;

    @PodamExclude
    @OneToOne(mappedBy = "adoption")
    private TrialCohabitationEntity trialCohabitation;
}
