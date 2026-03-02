package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.Entity;

/**
 * Entidad que representa a una mascota
 */
@Data
@Entity
public class PetEntity extends BaseEntity {

    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String sex;
    private String size;
    private String temperament;
    private String specialNeeds;
    private String status;
    private String description;
    private String photos; // URL o path
    private String activityLevel;
    private String origin; // Rescatado o nacido en refugio

    // Filtros de compatibilidad
    private Boolean isChildFriendly;
    private Boolean isPetFriendly;
    private String spaceRequired; // HOUSE, APARTMENT, BOTH
}
