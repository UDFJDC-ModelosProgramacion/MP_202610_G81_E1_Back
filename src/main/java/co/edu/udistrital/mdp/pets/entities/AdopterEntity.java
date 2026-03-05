package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.List;

@Data
@Entity
public class AdopterEntity extends UserEntity {

    private String housingType;
    private Boolean hasChildren;
    private Boolean hasOtherPets;

    @PodamExclude
    @OneToMany(mappedBy = "adopter")
    private List<AdoptionEntity> adoptions;
}
