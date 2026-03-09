package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface AdoptionFollowUpRepository extends JpaRepository<AdoptionFollowUpEntity, Long> {

    /**
     * Buscar seguimientos por adopción
     */
    List<AdoptionFollowUpEntity> findByAdoptionId(Long id);

    /**
     * Buscar seguimientos por veterinario
     */
    List<AdoptionFollowUpEntity> findByVeterinarianId(Long id);

    /**
     * Buscar seguimientos por frecuencia
     */
    List<AdoptionFollowUpEntity> findByFrequency(String frequency);

}
