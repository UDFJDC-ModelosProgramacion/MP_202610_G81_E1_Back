package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdopterRepository extends JpaRepository<AdopterEntity, Long> {

    List<AdopterEntity> findByHousingType(String housingType);

    List<AdopterEntity> findByHasChildrenAndHasOtherPets(Boolean hasChildren, Boolean hasOtherPets);
}
