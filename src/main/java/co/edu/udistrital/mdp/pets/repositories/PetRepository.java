package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Long> {

    /**
     * Nivel 1: Method Named Queries (Automáticos)
     * Spring genera el SQL basándose en el nombre del método.
     */

    // Buscar por especie (Requerimiento: filtros básicos)
    List<PetEntity> findBySpecies(String species);

    // Buscar por requisitos de espacio (Requerimiento: filtros avanzados - casa/apto)
    List<PetEntity> findBySpaceRequired(String spaceRequired);

    // Buscar por nivel de actividad
    List<PetEntity> findByActivityLevel(String activityLevel);

    // Buscar mascotas aptas para niños y otros animales
    List<PetEntity> findByGoodWithKidsTrueAndGoodWithPetsTrue();

    // Buscar por estado (Ej: para mostrar solo los "Disponibles")
    List<PetEntity> findByStatus(String status);
	
	// Filtro por rango de edad (Cachorros, Adultos, Seniors)
    List<PetEntity> findByAgeBetween(Integer minAge, Integer maxAge);
}
