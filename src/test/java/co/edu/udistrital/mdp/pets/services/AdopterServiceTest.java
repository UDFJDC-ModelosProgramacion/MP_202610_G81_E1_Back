package co.edu.udistrital.mdp.pets.services;

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

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdopterService.class)
public class AdopterServiceTest {

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<AdopterEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionRequestEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdopterEntity entity = factory.manufacturePojo(AdopterEntity.class);
            entity.setHousingType("House " + i);
            entity.setHasChildren(i % 2 == 0);
            entity.setHasOtherPets(i % 2 == 1);
            entity.setAdoptionRequests(new ArrayList<>());
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreateAdopter() throws IllegalOperationException {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setHousingType("Apartment");
        newEntity.setHasChildren(true);
        newEntity.setHasOtherPets(false);

        AdopterEntity result = adopterService.createAdopter(newEntity);

        assertNotNull(result);
        AdopterEntity entity = entityManager.find(AdopterEntity.class, result.getId());
        assertEquals(newEntity.getHousingType(), entity.getHousingType());
    }

    @Test
    void testCreateAdopterWithNullHasOtherPets() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setHousingType("House");
        newEntity.setHasChildren(true);
        newEntity.setHasOtherPets(null); // Null - debe fallar

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.createAdopter(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithNullHasChildren() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setHousingType("House");
        newEntity.setHasChildren(null); // Null - debe fallar
        newEntity.setHasOtherPets(false);

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.createAdopter(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithNullHousingType() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setHousingType(null); // Null - debe fallar
        newEntity.setHasChildren(true);
        newEntity.setHasOtherPets(false);

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.createAdopter(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithEmptyHousingType() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setHousingType("   "); // Blank - debe fallar
        newEntity.setHasChildren(true);
        newEntity.setHasOtherPets(false);

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.createAdopter(newEntity);
        });
    }

    // ==================== GET TESTS ====================

    @Test
    void testGetAdopters() {
        List<AdopterEntity> list = adopterService.getAdopters();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetAdopter() throws EntityNotFoundException {
        AdopterEntity entity = data.get(0);
        AdopterEntity resultEntity = adopterService.getAdopter(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getHousingType(), resultEntity.getHousingType());
    }

    @Test
    void testGetInvalidAdopter() {
        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.getAdopter(999L);
        });
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdateAdopter() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = data.get(0);
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setHousingType("Updated House");
        pojoEntity.setHasChildren(false);
        pojoEntity.setHasOtherPets(true);

        AdopterEntity resp = adopterService.updateAdopter(entity.getId(), pojoEntity);

        assertNotNull(resp);
        assertEquals(pojoEntity.getHousingType(), resp.getHousingType());
    }

    @Test
    void testUpdateAdopterWithNullHousingType() {
        AdopterEntity entity = data.get(0);
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setHousingType(null);

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.updateAdopter(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidAdopter() {
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setHousingType("House");

        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.updateAdopter(999L, pojoEntity);
        });
    }

    // ==================== DELETE TESTS ====================

    @Test
    void testDeleteAdopter() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = data.get(0);
        adopterService.deleteAdopter(entity.getId());
        AdopterEntity deleted = entityManager.find(AdopterEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidAdopter() {
        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.deleteAdopter(999L);
        });
    }

    @Test
    void testDeleteAdopterWithAdoptionRequests() {
        assertThrows(IllegalOperationException.class, () -> {
            AdopterEntity adopter = data.get(0);
            
            // Crear una solicitud de adopcion
            AdoptionRequestEntity request = factory.manufacturePojo(AdoptionRequestEntity.class);
            request.setAdopter(adopter);
            entityManager.persist(request);
            
            if (adopter.getAdoptionRequests() == null) {
                adopter.setAdoptionRequests(new ArrayList<>());
            }
            adopter.getAdoptionRequests().add(request);
            
            entityManager.flush();
            
            adopterService.deleteAdopter(adopter.getId());
        });
    }
}
