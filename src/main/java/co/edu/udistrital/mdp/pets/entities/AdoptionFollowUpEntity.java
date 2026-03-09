package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing the follow-up process of an adoption.
 * Linked to a veterinarian for check-ups and observations.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdoptionFollowUpEntity extends BaseEntity {

    private String frequency;
    private String notes;

    // Relation: 1 Veterinarian can perform many Follow-ups
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity veterinarian;

    // Relation: 1 Adoption (Composition) has many Follow-ups
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adoption_id")
    private AdoptionEntity adoption;

    // Relation: 1 Pet has an assigned veterinarian for follow-ups
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    public void scheduleCheckUp() {
        // logic to schedule a check-up
    }

    public void recordObservation() {
        // logic to record an observation
    }
}
