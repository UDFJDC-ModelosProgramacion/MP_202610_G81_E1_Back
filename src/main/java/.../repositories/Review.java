package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    // Reviews por pet/shelter (si agregas foreign key)
    // List<ReviewEntity> findByPetIdOrderByDateDesc(Long petId);

    // Promedio de rating
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r")
    Double getAverageRating();

    // Reviews recientes
    List<ReviewEntity> findByOrderByDateDesc();
}
