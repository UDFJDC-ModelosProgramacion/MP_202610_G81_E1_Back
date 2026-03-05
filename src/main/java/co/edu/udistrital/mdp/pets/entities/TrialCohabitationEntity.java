package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
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
    @OneToOne
    private AdoptionEntity adoption;
}
