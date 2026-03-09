package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequestEntity, Long> {

    /**
     * Buscar solicitudes por estado
     */
    List<AdoptionRequestEntity> findByStatus(String status);

    /**
     * Buscar solicitudes por fecha
     */
    List<AdoptionRequestEntity> findByRequestDate(LocalDate requestDate);

    /**
     * Buscar solicitudes por mascota
     */
    List<AdoptionRequestEntity> findByPetPetId(Long petId);

}
