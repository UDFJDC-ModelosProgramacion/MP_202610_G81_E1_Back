package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    // Mensajes no leídos por usuario (si agregas userId)
    // List<MessageEntity> findByUserIdAndIsReadFalse(Long userId);

    // Mensajes recientes
    List<MessageEntity> findByIsReadFalseOrderByTimestampDesc();
}
