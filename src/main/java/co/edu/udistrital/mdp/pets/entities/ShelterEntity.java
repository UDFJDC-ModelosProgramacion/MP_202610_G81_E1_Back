package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa a un Refugio (Shelter)
 */
@Data
@Entity
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;
    private String description;
    private String email;
    
    // 'gallery' como Long String, usamos String de Java
    // que JPA mapeará a TEXT o CLOB según la DB
    private String gallery;

	@PodamExclude
	@OneToMany(mappedBy = "shelter")
	private List<VeterinarianEntity> veterinarians = new ArrayList<>();
}
