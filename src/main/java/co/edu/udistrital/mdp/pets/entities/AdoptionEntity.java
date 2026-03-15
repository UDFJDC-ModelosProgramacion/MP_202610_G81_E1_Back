package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing an Adoption process.
 * Manages the relationship between an Adopter, a Pet, and its follow-ups.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdoptionEntity extends BaseEntity {

    private LocalDate adoptionDate;

	@Enumerated(EnumType.STRING)
	private ProcessStatus status;

    // Relation: N:1 Adopter has many Adoptions
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private AdopterEntity adopter;

    // Relation N:1 Pet has many Adoptions (historical or current)
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    // Relation 1:1 with TrialCohabitation
    @PodamExclude
    @OneToOne(mappedBy = "adoption")
    private TrialCohabitationEntity trialCohabitation;

    // Relation 1:N with AdoptionFollowUp (Composition)
    @PodamExclude
    @OneToMany(mappedBy = "adoption", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AdoptionFollowUpEntity> followUps;
}
