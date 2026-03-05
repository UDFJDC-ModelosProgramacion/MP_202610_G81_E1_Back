package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class AdopterEntity extends BaseEntity {

    private String housingType;
    private Boolean hasChildren;
    private Boolean hasOtherPets;
}