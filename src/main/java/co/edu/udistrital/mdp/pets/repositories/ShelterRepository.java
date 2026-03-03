package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {

    /**
     * Busca refugios por ciudad (Method Named Query)
     */
    List<ShelterEntity> findByCity(String city);

    /**
     * Busca un refugio por su nombre exacto
     */
    ShelterEntity findByName(String name);
}
