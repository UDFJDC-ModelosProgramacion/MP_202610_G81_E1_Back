package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionService.class)
class AdoptionServiceTest {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<AdoptionEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionFollowUpEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdoptionEntity entity = factory.manufacturePojo(AdoptionEntity.class);
            entity.setAdoptionDate(LocalDate.now().minusDays(i));
            entity.setFollowUps(new ArrayList<>());
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreateAdoption() throws IllegalOperationException {
        AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
        newEntity.setAdoptionDate(LocalDate.now());

        AdoptionEntity result = adoptionService.createAdoption(newEntity);

        assertNotNull(result);
        AdoptionEntity entity = entityManager.find(AdoptionEntity.class, result.getId());
        assertEquals(newEntity.getAdoptionDate(), entity.getAdoptionDate());
    }

    @Test
    void testCreateAdoptionWithNullDate() {
        AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
        newEntity.setAdoptionDate(null); // Null - debe fallar

        assertThrows(IllegalOperationException.class, () -> {
            adoptionService.createAdoption(newEntity);
        });
    }

    // ==================== GET TESTS ====================

    @Test
    void testGetAdoptions() {
        List<AdoptionEntity> list = adoptionService.getAdoptions();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetAdoption() throws EntityNotFoundException {
        AdoptionEntity entity = data.get(0);
        AdoptionEntity resultEntity = adoptionService.getAdoption(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getAdoptionDate(), resultEntity.getAdoptionDate());
    }

    @Test
    void testGetInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            adoptionService.getAdoption(999L);
        });
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdateAdoption() throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = data.get(0);
        AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
        pojoEntity.setAdoptionDate(entity.getAdoptionDate()); // Misma fecha - debe permitir

        AdoptionEntity resp = adoptionService.updateAdoption(entity.getId(), pojoEntity);

        assertNotNull(resp);
    }

    @Test
    void testUpdateAdoptionCannotChangeDate() {
        AdoptionEntity entity = data.get(0);
        AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
        pojoEntity.setAdoptionDate(LocalDate.now().plusDays(10)); // Fecha diferente - debe fallar

        assertThrows(IllegalOperationException.class, () -> {
            adoptionService.updateAdoption(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidAdoption() {
        AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
        pojoEntity.setAdoptionDate(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> {
            adoptionService.updateAdoption(999L, pojoEntity);
        });
    }

    // ==================== DELETE TESTS ====================

    @Test
    void testDeleteAdoption() throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = data.get(0);
        adoptionService.deleteAdoption(entity.getId());
        AdoptionEntity deleted = entityManager.find(AdoptionEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            adoptionService.deleteAdoption(999L);
        });
    }

    @Test
    void testDeleteAdoptionWithFollowUps() {
        assertThrows(IllegalOperationException.class, () -> {
            AdoptionEntity adoption = data.get(0);
            
            // Crear un seguimiento
            AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);
            followUp.setAdoption(adoption);
            entityManager.persist(followUp);
            
            if (adoption.getFollowUps() == null) {
                adoption.setFollowUps(new ArrayList<>());
            }
            adoption.getFollowUps().add(followUp);
            
            entityManager.flush();
            
            adoptionService.deleteAdoption(adoption.getId());
        });
    }

    @Test
    void testCreateAdoptionWithNullAdoptionEntity() {
        assertThrows(IllegalOperationException.class, () -> {
            adoptionService.createAdoption(null);
        });
    }

    @Test
    void testUpdateAdoptionWithNullNewDateSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = data.get(0);
        AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
        pojoEntity.setAdoptionDate(null); // Set new adoption date to null

        AdoptionEntity resp = adoptionService.updateAdoption(entity.getId(), pojoEntity);

        assertNotNull(resp);
        // The original adoption date should be preserved
        assertEquals(entity.getAdoptionDate(), resp.getAdoptionDate()); 
    }

    @Test
    void testDeleteAdoptionWithNullFollowUpsSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity adoption = data.get(0);
        adoption.setFollowUps(null); // Set follow-ups to null
        entityManager.persist(adoption);
        entityManager.flush();

        adoptionService.deleteAdoption(adoption.getId());
        AdoptionEntity deleted = entityManager.find(AdoptionEntity.class, adoption.getId());
        assertNull(deleted);
    }
}
