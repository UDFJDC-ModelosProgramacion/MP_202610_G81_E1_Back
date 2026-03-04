package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import lombok.ToString;
import lombok.EqualsAndHashCode;
/**
 * Entidad que representa a un Refugio (Shelter)
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
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
	@ToString.Exclude
	private List<VeterinarianEntity> veterinarians = new ArrayList<>();
}
