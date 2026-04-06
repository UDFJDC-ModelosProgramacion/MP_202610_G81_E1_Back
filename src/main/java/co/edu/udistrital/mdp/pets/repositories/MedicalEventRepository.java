package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;

public interface MedicalEventRepository extends JpaRepository<MedicalEventEntity, Long> {
    List<MedicalEventEntity> findByMedicalHistoryId(Long medicalHistoryId);
}