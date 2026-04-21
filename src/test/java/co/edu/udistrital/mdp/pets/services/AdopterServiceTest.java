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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({AdopterService.class, ModelMapper.class})
@EntityScan(basePackages = "co.edu.udistrital.mdp.pets.entities")
class AdopterServiceTest {

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<AdopterEntity> data = new ArrayList<>();

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionRequestEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdopterEntity entity = factory.manufacturePojo(AdopterEntity.class);
            entity.setEmail("adopter" + i + "@test.com");
            entity.setPhone("300555666" + i);
            entity.setName("Adopter " + i);
            entity.setPassword("password123");
            entity.setHousingType("House " + i);
            entity.setHasChildren(i % 2 == 0);
            entity.setHasOtherPets(i % 2 == 1);
            entity.setAdoptionRequests(new ArrayList<>());
            entity.setAdoptions(new ArrayList<>());
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreateAdopterSuccess() throws IllegalOperationException {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setEmail("nuevo_adopter@test.com");
        newEntity.setPhone("3101234567");
        newEntity.setName("Juan Perez");
        newEntity.setPassword("admin123");
        newEntity.setHousingType("Apartamento");
        newEntity.setHasChildren(true);
        newEntity.setHasOtherPets(false);

        AdopterEntity result = (AdopterEntity) adopterService.createUser(newEntity);

        assertNotNull(result);
        assertEquals("3101234567", result.getPhone());
        assertEquals("Apartamento", result.getHousingType());
    }

    @Test
    void testCreateAdopterWithNullHasOtherPets() {
        assertThrows(IllegalOperationException.class, () -> {
            AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
            newEntity.setEmail("test@test.com");
            newEntity.setPhone("3101234567");
            newEntity.setName("Test");
            newEntity.setPassword("password");
            newEntity.setHousingType("Casa");
            newEntity.setHasChildren(true);
            newEntity.setHasOtherPets(null);
            adopterService.createUser(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithNullHasChildren() {
        assertThrows(IllegalOperationException.class, () -> {
            AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
            newEntity.setEmail("test@test.com");
            newEntity.setPhone("3101234567");
            newEntity.setName("Test");
            newEntity.setPassword("password");
            newEntity.setHousingType("Casa");
            newEntity.setHasChildren(null);
            newEntity.setHasOtherPets(false);
            adopterService.createUser(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithNullHousingType() {
        assertThrows(IllegalOperationException.class, () -> {
            AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
            newEntity.setEmail("test@test.com");
            newEntity.setPhone("3101234567");
            newEntity.setName("Test");
            newEntity.setPassword("password");
            newEntity.setHousingType(null);
            newEntity.setHasChildren(true);
            newEntity.setHasOtherPets(false);
            adopterService.createUser(newEntity);
        });
    }

    @Test
    void testCreateAdopterWithEmptyHousingType() {
        assertThrows(IllegalOperationException.class, () -> {
            AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
            newEntity.setEmail("test@test.com");
            newEntity.setPhone("3101234567");
            newEntity.setName("Test");
            newEntity.setPassword("password");
            newEntity.setHousingType("   ");
            newEntity.setHasChildren(true);
            newEntity.setHasOtherPets(false);
            adopterService.createUser(newEntity);
        });
    }

    // ==================== GET TESTS ====================

    @Test
    void testGetAdopters() {
        List<?> list = adopterService.getUsers();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetAdopter() throws EntityNotFoundException {
        AdopterEntity entity = data.get(0);
        AdopterEntity resultEntity = (AdopterEntity) adopterService.getUser(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getHousingType(), resultEntity.getHousingType());
    }

    @Test
    void testGetInvalidAdopter() {
        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.getUser(999L);
        });
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdateAdopterSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = data.get(0);
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setEmail("updated@test.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setHousingType("Updated House");
        pojoEntity.setHasChildren(false);
        pojoEntity.setHasOtherPets(true);

        AdopterEntity resp = (AdopterEntity) adopterService.updateUser(entity.getId(), pojoEntity);

        assertNotNull(resp);
        assertEquals("Updated House", resp.getHousingType());
    }

    @Test
    void testUpdateAdopterWithNullHousingType() {
        AdopterEntity entity = data.get(0);
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setEmail("updated@test.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setHousingType(null);
        pojoEntity.setHasChildren(true);
        pojoEntity.setHasOtherPets(false);

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.updateUser(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidAdopter() {
        AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
        pojoEntity.setEmail("test@test.com");
        pojoEntity.setPhone("3101234567");
        pojoEntity.setName("Test");
        pojoEntity.setPassword("password");
        pojoEntity.setHousingType("Casa");
        pojoEntity.setHasChildren(true);
        pojoEntity.setHasOtherPets(false);

        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.updateUser(999L, pojoEntity);
        });
    }

    // ==================== DELETE TESTS ====================

    @Test
    void testDeleteAdopterSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = data.get(0);
        adopterService.deleteUser(entity.getId());
        
        AdopterEntity deleted = entityManager.find(AdopterEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidAdopter() {
        assertThrows(EntityNotFoundException.class, () -> {
            adopterService.deleteUser(999L);
        });
    }

    @Test
    void testDeleteAdopterWithAdoptionRequests() {
        AdopterEntity adopter = data.get(0);

        AdoptionRequestEntity request = factory.manufacturePojo(AdoptionRequestEntity.class);
        request.setAdopter(adopter);
        
        if (adopter.getAdoptionRequests() == null) {
            adopter.setAdoptionRequests(new ArrayList<>());
        }
        adopter.getAdoptionRequests().add(request);

        entityManager.persist(request);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.deleteUser(adopter.getId());
        });
    }

    @Test
    void testDeleteAdopterWithAdoptions() {
        AdopterEntity adopter = data.get(1);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setAdopter(adopter);
        
        if (adopter.getAdoptions() == null) {
            adopter.setAdoptions(new ArrayList<>());
        }
        adopter.getAdoptions().add(adoption);

        entityManager.persist(adoption);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            adopterService.deleteUser(adopter.getId());
        });
    }

    @Test
    void testDeleteAdopterWithNullAdoptionRequestsSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity adopter = data.get(0);
        adopter.setAdoptionRequests(null); // Set requests to null
        adopter.setAdoptions(null); // Ensure adoptions is null too for this test
        entityManager.persist(adopter);
        entityManager.flush();

        adopterService.deleteUser(adopter.getId());
        AdopterEntity deleted = entityManager.find(AdopterEntity.class, adopter.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteAdopterWithNullAdoptionsSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity adopter = data.get(1);
        adopter.setAdoptions(null); // Set adoptions to null
        adopter.setAdoptionRequests(null); // Ensure requests is null too for this test
        entityManager.persist(adopter);
        entityManager.flush();

        adopterService.deleteUser(adopter.getId());
        AdopterEntity deleted = entityManager.find(AdopterEntity.class, adopter.getId());
        assertNull(deleted);
    }
}
