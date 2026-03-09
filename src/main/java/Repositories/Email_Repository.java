// EmailRepository.java
package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
}
