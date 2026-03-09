package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
 
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Buscar cualquier usuario por email 
    Optional<UserEntity> findByEmail(String email);

    // Verificar si un teléfono ya existe
    boolean existsByPhone(String phone);
	
    // Buscar cualquier usuario por telefono
    Optional<UserEntity> findByPhone(String phone);
}
