package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;

@Repository
public interface TrialCohabitationRepository extends JpaRepository<TrialCohabitationEntity, Long> {

    List<TrialCohabitationEntity> findByResult(String result);

    TrialCohabitationEntity findByAdoptionId(Long adoptionId);
}
