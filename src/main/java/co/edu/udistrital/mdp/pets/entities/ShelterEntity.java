package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.ArrayList;
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;
    private String description;
    private String email;
    private String gallery;

	// Relation 1:N with Veterinarian (Agregattion)
    @PodamExclude
    @OneToMany(mappedBy = "shelter")
    @ToString.Exclude
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    // Relation 1:N with ShelterEvent
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<ShelterEventEntity> events = new ArrayList<>();

    // Relation 1:N with Report 
    @PodamExclude
    @OneToMany
	@JoinColumn(name = "shelter_id")
    private List<ReportEntity> reports = new ArrayList<>();

	// Relation 1:N with Message 
    @PodamExclude
    @OneToMany(mappedBy = "shelter")
    private List<MessageEntity> messages = new ArrayList<>();

	// Relation 1:N with Pet (Composition)
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PetEntity> pets = new ArrayList<>();
}
