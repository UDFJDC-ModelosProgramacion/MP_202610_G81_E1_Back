package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.Date;

/**
 * Entity representing a Review.
 * Linked to an Adopter and a Pet.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ReviewEntity extends BaseEntity {

    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Temporal(TemporalType.DATE)
    private Date date;

	// Relation M:1 with Adopter
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private AdopterEntity adopter;

	// Relation M:1 with Pet
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;


	@PrePersist
	protected void onCreate() {
		this.date = new Date();
	}

    public String getSummary() {
        return "Rating: " + rating + " - " + comment;
    }
}
