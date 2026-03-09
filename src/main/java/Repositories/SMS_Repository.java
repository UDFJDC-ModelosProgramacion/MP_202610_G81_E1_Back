// SMERepository.java
package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.SMSEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SMERepository extends JpaRepository<SMSEntity, Long> {
}
