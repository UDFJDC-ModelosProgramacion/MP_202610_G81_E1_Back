package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;
    private String description;
    private String email;
    private String gallery;

    @PodamExclude
    @OneToMany(mappedBy = "shelter")
    @ToString.Exclude
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    // Relation 1:N with ShelterEvent
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<ShelterEventEntity> events = new ArrayList<>();

    // Relation 1:N with Report (Direct reports from Shelter)
    @PodamExclude
    @OneToMany
	@JoinColumn(name = "shelter_id")
    private List<ReportEntity> reports = new ArrayList<>();
}
