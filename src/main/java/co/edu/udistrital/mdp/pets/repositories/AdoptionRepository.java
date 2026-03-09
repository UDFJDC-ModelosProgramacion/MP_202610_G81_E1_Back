package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
@Repository
public interface AdoptionRepository extends JpaRepository<AdoptionEntity, Long> {

    List<AdoptionFollowUpEntity> findByAdoptionId(Long id);
    List<AdoptionFollowUpEntity> findByPetId(Long id);
}
