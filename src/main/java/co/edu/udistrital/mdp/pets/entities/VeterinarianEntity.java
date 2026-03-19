package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import lombok.ToString;

import java.util.List;
import java.util.ArrayList;
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class VeterinarianEntity extends UserEntity {

    private String specialty;   // Especialidad médica
    private String availability; // Disponibilidad horaria

	@PodamExclude
	@ManyToOne
	@ToString.Exclude
	private ShelterEntity shelter;

	@PodamExclude
    @OneToMany(mappedBy = "veterinarian") 
    private List<VaccinationRecordEntity> vaccinationRecords = new ArrayList<>();

	@PodamExclude
    @OneToMany(mappedBy = "veterinarian") 
    private List<MedicalEventEntity> medicalEvents = new ArrayList<>(); 
	
	@PodamExclude
	@OneToMany(mappedBy = "veterinarian") 
	private List<AdoptionFollowUpEntity> adoptionFollowUps = new ArrayList<>();
}
