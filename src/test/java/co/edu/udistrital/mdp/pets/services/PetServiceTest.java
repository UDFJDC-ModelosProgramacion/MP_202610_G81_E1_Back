package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(PetService.class)
public class PetServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<PetEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from MedicalHistoryEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PetEntity entity = factory.manufacturePojo(PetEntity.class);
            entity.setStatus(PetStatus.AVAILABLE);
            entity.setAge(i + 1); // Edad > 0
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test
    void testCreatePetSuccess() throws IllegalOperationException {
        PetEntity newPet = factory.manufacturePojo(PetEntity.class);
        newPet.setAge(2);
        newPet.setStatus(PetStatus.AVAILABLE);
        newPet.setGoodWithKids(true);
        newPet.setGoodWithPets(true);

        PetEntity result = petService.createPet(newPet);

        assertNotNull(result);
        assertNotNull(result.getMedicalHistory()); // Verifica composición
        assertEquals(result, result.getMedicalHistory().getPet());
    }

    @Test
    void testCreatePetInvalidAge() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity newPet = factory.manufacturePojo(PetEntity.class);
            newPet.setAge(0); // Regla: Age > 0
            petService.createPet(newPet);
        });
    }

    @Test
    void testUpdateStatusToAdoptedIllegal() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = data.get(0);
            pet.setStatus(PetStatus.MEDICAL_TREATMENT);
            entityManager.persist(pet);

            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.ADOPTED); // No se puede pasar de Treatment a Adopted
            updateData.setAge(5);
            
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testUpdateWithActiveTrialCollision() {
        PetEntity pet = data.get(0);
        
        // Simular un proceso de prueba activo
        TrialCohabitationEntity activeTrial = factory.manufacturePojo(TrialCohabitationEntity.class);
        activeTrial.setStatus(ProcessStatus.IN_PROGRESS);
        activeTrial.setPet(pet);
        
        if (pet.getTrials() == null) pet.setTrials(new ArrayList<>());
        pet.getTrials().add(activeTrial);
        
        entityManager.persist(activeTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.IN_TRIAL); // Debería fallar por el trial activo
            updateData.setAge(3);
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testDeletePetWithHistory() {
        PetEntity pet = data.get(1);
        
        // Simular historial de pruebas (aunque estén terminadas)
        TrialCohabitationEntity oldTrial = factory.manufacturePojo(TrialCohabitationEntity.class);
        oldTrial.setStatus(ProcessStatus.COMPLETED);
        oldTrial.setPet(pet);
        
        if (pet.getTrials() == null) pet.setTrials(new ArrayList<>());
        pet.getTrials().add(oldTrial);
        
        entityManager.persist(oldTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            petService.deletePet(pet.getId());
        });
    }
}
