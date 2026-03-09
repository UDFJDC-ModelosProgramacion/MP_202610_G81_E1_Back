package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShelterEventRepository extends JpaRepository<ShelterEventEntity, Integer> {

    List<ShelterEventEntity> findByDate(LocalDate date);
    
    List<ShelterEventEntity> findByTitleContainingIgnoreCase(String title);
}
