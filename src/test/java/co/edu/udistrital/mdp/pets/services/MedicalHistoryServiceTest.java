package co.edu.udistrital.mdp.pets.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(MedicalHistoryService.class)
class MedicalHistoryServiceTest {

    @Autowired
    private MedicalHistoryService service;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<MedicalHistoryEntity> data = new ArrayList<>();

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from MedicalHistoryEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.clear();
    }

    private void insertData() {
        data.clear();
        for (int i = 0; i < 3; i++) {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            entityManager.persist(pet);

            MedicalHistoryEntity history = factory.manufacturePojo(MedicalHistoryEntity.class);
            history.setPet(pet);
            history.setNotes("Initial notes " + i);
            entityManager.persist(history);

            data.add(history);
        }
        entityManager.flush();
    }

    // ==========================================
    // CREATION
    // ==========================================

    @Test
    void testCreateHistorySuccess() throws IllegalOperationException {
        MedicalHistoryEntity newHistory = factory.manufacturePojo(MedicalHistoryEntity.class);
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        entityManager.flush();

        newHistory.setPet(pet);
        newHistory.setNotes("New history");

        MedicalHistoryEntity result = service.createMedicalHistory(newHistory);
        assertNotNull(result.getId());
        assertEquals(pet.getId(), result.getPet().getId());
    }

    @Test
    void testCreateNullHistoryFail() {
        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createMedicalHistory(null));
        assertNotNull(ex);
    }

    @Test
    void testCreateWithoutPetFail() {
        MedicalHistoryEntity newHistory = factory.manufacturePojo(MedicalHistoryEntity.class);
        newHistory.setPet(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createMedicalHistory(newHistory));
        assertNotNull(ex);
    }

    @Test
    void testCreateSecondHistoryForSamePetFails() {
        MedicalHistoryEntity existing = data.get(0);
        MedicalHistoryEntity second = factory.manufacturePojo(MedicalHistoryEntity.class);
        second.setPet(existing.getPet());
        second.setNotes("Second history");

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createMedicalHistory(second));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("already has") || ex.getMessage().toLowerCase().contains("assigned"));
    }

    // ==========================================
    // READ
    // ==========================================

    @Test
    void testGetMedicalHistorySuccess() throws EntityNotFoundException {
        MedicalHistoryEntity expected = data.get(0);
        MedicalHistoryEntity result = service.getMedicalHistory(expected.getId());
        assertEquals(expected.getId(), result.getId());
    }

    // ==========================================
    // UPDATE
    // ==========================================

    @Test
    void testUpdateHistorySuccess() throws EntityNotFoundException, IllegalOperationException {
        MedicalHistoryEntity existing = data.get(0);
        MedicalHistoryEntity updateData = factory.manufacturePojo(MedicalHistoryEntity.class);
        updateData.setPet(existing.getPet());
        updateData.setNotes("Updated test notes");

        MedicalHistoryEntity result = service.updateMedicalHistory(existing.getId(), updateData);
        assertNotNull(result);
        assertEquals(existing.getId(), result.getId());
        assertEquals("Updated test notes", result.getNotes());
    }

    @Test
    void testUpdateHistoryNotFound() {
        MedicalHistoryEntity updateData = factory.manufacturePojo(MedicalHistoryEntity.class);
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        entityManager.flush();
        updateData.setPet(pet);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.updateMedicalHistory(123456789L, updateData));
        assertNotNull(ex);
    }

    @Test
    void testUpdateHistoryWithoutPetFails() {
        MedicalHistoryEntity existing = data.get(1);
        MedicalHistoryEntity updateData = factory.manufacturePojo(MedicalHistoryEntity.class);
        updateData.setPet(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.updateMedicalHistory(existing.getId(), updateData));
        assertNotNull(ex);
    }

    // ==========================================
    // DELETE
    // ==========================================

    @Test
    void testDeleteHistorySuccess() throws EntityNotFoundException {
        MedicalHistoryEntity existing = data.get(2);
        service.deleteMedicalHistory(existing.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getMedicalHistory(existing.getId()));
        assertNotNull(ex);
    }

    @Test
    void testDeleteHistoryNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.deleteMedicalHistory(999999L));
        assertNotNull(ex);
    }
}