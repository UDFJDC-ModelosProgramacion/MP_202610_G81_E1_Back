package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.ArrayList;

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
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private String spaceRequired; // HOUSE, APARTMENT, BOTH

	// Relation N:1 with Shelter
	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter; 

	// Relation 1:1 with MedicalHistory (Composition)
	@PodamExclude
	@OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
	private MedicalHistoryEntity medicalHistory;

	// Relation 1:M with Review
	@PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<ReviewEntity> reviews = new ArrayList<>();
}
