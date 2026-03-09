package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import lombok.ToString;
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
}
