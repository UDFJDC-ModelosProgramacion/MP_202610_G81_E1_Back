package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.jemos.podam.common.PodamExclude;

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
    private LocalDate followUpDate;

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
}
