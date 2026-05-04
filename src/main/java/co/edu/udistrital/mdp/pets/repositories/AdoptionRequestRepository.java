package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {

    /**
     * Busca solicitudes por el ID del adoptante y el ID de la mascota.
     * Spring Data JPA entiende esta convención: Arrastra el ID desde las entidades relacionadas.
     */
    @Query("SELECT r FROM AdoptionRequestEntity r WHERE r.adopter.id = :adopterId AND r.pet.id = :petId")
    List<AdoptionRequestEntity> findByAdopterIdAndPetId(
        @Param("adopterId") Long adopterId, 
        @Param("petId") Long petId
    );

    List<AdoptionRequestEntity> findByPetId(Long petId);
}
