package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.*;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MedicalHistoryEntity extends BaseEntity {

    private String description;

	@PodamExclude
    @OneToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
	
	@PodamExclude
    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinationRecordEntity> vaccinationRecords;

	@PodamExclude
    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalEventEntity> medicalEvents;
}
