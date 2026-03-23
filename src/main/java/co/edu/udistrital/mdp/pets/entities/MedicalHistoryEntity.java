package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MedicalHistoryEntity extends BaseEntity {

    private String description;
	private LocalDate lastCheckout;

	@PodamExclude
    @OneToOne
    @JoinColumn(name = "pet_id", unique = true)
    private PetEntity pet;

    @Column(length = 1000)
    private String notes;
	
	@PodamExclude
    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinationRecordEntity> vaccinationRecords;

	@PodamExclude
    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalEventEntity> medicalEvents;
}
