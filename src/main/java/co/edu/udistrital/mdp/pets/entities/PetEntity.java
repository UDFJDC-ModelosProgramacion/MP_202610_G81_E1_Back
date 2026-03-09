package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

	// Relation N:1 with Shelter (Composicion desde Shelter)
    @PodamExclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter; 

    // Relation 1:1 with MedicalHistory (Composicion)
    @PodamExclude
    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalHistoryEntity medicalHistory;

    // Relation 1:N with Review
    @PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<ReviewEntity> reviews;

    // Relation 1:N with Adoption
    @PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<AdoptionEntity> adoptions;

    // Relation 1:N with AdoptionRequest
    @PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<AdoptionRequestEntity> adoptionRequests;

    // Relation 1:N with AdoptionFollowUp
    @PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<AdoptionFollowUpEntity> followUps;

    // Relation 1:N with TrialCohabitation
    @PodamExclude
    @OneToMany(mappedBy = "pet")
    private List<TrialCohabitationEntity> trials;
}
