package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.jemos.podam.common.PodamExclude;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccinationRecordEntity extends BaseEntity {

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "vaccination_date")
    private LocalDate vaccinationDate;

	@PodamExclude
	@ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "medical_history_id")
    private MedicalHistoryEntity medicalHistory;

	@PodamExclude
    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private VaccineEntity vaccine;

	@PodamExclude
	@ManyToOne
	@JoinColumn(name= "veterinarian_id")
	private VeterinarianEntity veterinarian;
}
