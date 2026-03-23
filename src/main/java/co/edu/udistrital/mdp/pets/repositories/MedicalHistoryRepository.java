package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistoryEntity, Long> {
    boolean existsByPetId(Long petId);
}
