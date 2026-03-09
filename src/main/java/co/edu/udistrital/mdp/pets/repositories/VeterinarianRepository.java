package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VeterinarianRepository extends JpaRepository<VeterinarianEntity, Long> {

    // Buscar por especialidad para asignar seguimientos
    List<VeterinarianEntity> findBySpecialty(String specialty);

    // Buscar por disponibilidad (Mañana/Tarde/Fines de semana)
    List<VeterinarianEntity> findByAvailabilityContaining(String availability);
}
