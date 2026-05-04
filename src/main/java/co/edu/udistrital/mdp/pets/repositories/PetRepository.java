package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Long> {

    List<PetEntity> findBySpecies(String species);

    List<PetEntity> findBySpaceRequired(String spaceRequired);

    List<PetEntity> findByActivityLevel(String activityLevel);

    List<PetEntity> findByGoodWithKidsTrueAndGoodWithPetsTrue();

    List<PetEntity> findByStatus(String status);

	// query de filtrados 
    @Query("SELECT p FROM PetEntity p WHERE " +
           "(:species IS NULL OR p.species = :species) AND " +
           "(:size IS NULL OR p.size = :size) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<PetEntity> findByFilters(
            @Param("species") String species, 
            @Param("size") String size,
            @Param("status") PetStatus status);
}

