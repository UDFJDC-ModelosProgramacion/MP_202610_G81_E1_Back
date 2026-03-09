// no hay nada en adopter, el encargado de adopter debe reemplazar estos archivos por sus AdopterEntity y AdopterRepository

package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdopterRepository extends JpaRepository<MessageEntity, Long> {
}
