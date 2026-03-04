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

	 /**
     * El método del Observer se deja vacío en la Entidad.
     * La lógica se inyectará o ejecutará desde la capa de Service.
     */
    @Override
    public void update(String notification) {
        // Se deja vacío para cumplir con la jerarquía 
        // sin ensuciar la entidad con lógica de negocio.
    }

	@PodamExclude
	@ManyToOne
	@ToString.Exclude
	private ShelterEntity shelter;
}
