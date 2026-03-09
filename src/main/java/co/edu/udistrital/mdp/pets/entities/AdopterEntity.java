package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.List;

/**
 * Entity representing an Adopter, extending User details.
 * Implements the Observer pattern update method.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class AdopterEntity extends UserEntity {

    private String housingType;
    private Boolean hasChildren;
    private Boolean hasOtherPets;

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.ALL)
    private List<AdoptionEntity> adoptions; 

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.ALL)
    private List<MessageEntity> messages;  

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.ALL)
    private List<ReviewEntity> reviews; 

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.ALL)
    private List<AdoptionRequestEntity> adoptionRequests;
}
