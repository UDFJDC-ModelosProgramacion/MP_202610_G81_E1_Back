package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@DataJpaTest
@Transactional
@Import(VaccinationRecordService.class)
@EntityScan("co.edu.udistrital.mdp.pets.entities") // Explicitly scan for entities
class VaccinationRecordServiceTest {

    @Autowired
    private VaccinationRecordService vaccinationRecordService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<VaccinationRecordEntity> recordsData = new ArrayList<>();

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from VaccinationRecordEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VaccineEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.clear();
    }

    private void insertData() {
        recordsData.clear();
        for (int i = 0; i < 3; i++) {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            entityManager.persist(pet);

            VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
            vaccine.setName(vaccine.getName() == null ? "Vaccine" + i : vaccine.getName());
            entityManager.persist(vaccine);

            VaccinationRecordEntity entity = factory.manufacturePojo(VaccinationRecordEntity.class);
            entity.setApplicationDate(LocalDate.now().minusDays(i + 1));
            entity.setNextDueDate(entity.getApplicationDate().plusMonths(1));
            entity.setPet(pet);
            entity.setVaccine(vaccine);

            entityManager.persist(entity);
            recordsData.add(entity);
        }
        entityManager.flush();
    }

    // ==========================================
    // CREATION
    // ==========================================

    @Test
    void testCreateRecordSuccess() throws IllegalOperationException, EntityNotFoundException { 
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        vaccine.setName(vaccine.getName() == null ? "Parvovirus" : vaccine.getName());
        entityManager.persist(vaccine);
        newRecord.setVaccine(vaccine);

        newRecord.setNextDueDate(newRecord.getApplicationDate().plusMonths(1));

        VaccinationRecordEntity result = vaccinationRecordService.createVaccinationRecord(newRecord);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(newRecord.getApplicationDate(), result.getApplicationDate());
        assertEquals(pet.getId(), result.getPet().getId());
        assertEquals(vaccine.getId(), result.getVaccine().getId());
    }

    @Test
    void testCreateRecordWithNullApplicationDateFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNullNextDueDateFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());
        newRecord.setNextDueDate(null);

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        entityManager.persist(vaccine);
        newRecord.setVaccine(vaccine);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithFutureDateFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now().plusDays(5));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNextDueBeforeApplicationFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());
        newRecord.setNextDueDate(LocalDate.now().minusDays(1)); 

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        entityManager.persist(vaccine);
        newRecord.setVaccine(vaccine);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNullPetFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());
        newRecord.setPet(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNullVaccineFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        newRecord.setVaccine(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

	@Test
    void testCreateRecordWithNonexistentVaccineFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        VaccineEntity fakeVaccine = new VaccineEntity();
        fakeVaccine.setId(999999L); 
        newRecord.setVaccine(fakeVaccine);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        
        assertNotNull(ex);
    }

    @Test
    void createVaccinationRecordWithNullVaccineIdFails() {
        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setApplicationDate(LocalDate.now());
        newRecord.setNextDueDate(LocalDate.now().plusMonths(1));
        
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newRecord.setPet(pet);

        VaccineEntity vaccineWithoutId = factory.manufacturePojo(VaccineEntity.class);
        vaccineWithoutId.setId(null);
        newRecord.setVaccine(vaccineWithoutId);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(newRecord));
        assertNotNull(ex);
    }

    @Test
    void createVaccinationRecordNullFails() {
        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(null));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    // ==========================================
    // READ
    // ==========================================

    @Test
    void testGetVaccinationRecords() {
        List<VaccinationRecordEntity> list = vaccinationRecordService.getVaccinationRecords();
        assertNotNull(list);
        assertEquals(recordsData.size(), list.size());
    }

    @Test
    void testGetVaccinationRecordSuccess() throws EntityNotFoundException {
        VaccinationRecordEntity expected = recordsData.get(0);
        VaccinationRecordEntity result = vaccinationRecordService.getVaccinationRecord(expected.getId());

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
    }

    @Test
    void testGetVaccinationRecordNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccinationRecordService.getVaccinationRecord(9999L));
        assertNotNull(ex);
    }

    // ==========================================
    // UPDATE
    // ==========================================

    @Test
    void testUpdateRecordSuccess() throws EntityNotFoundException, IllegalOperationException {
        VaccinationRecordEntity existingRecord = recordsData.get(0);

        VaccinationRecordEntity updateData = factory.manufacturePojo(VaccinationRecordEntity.class);
        updateData.setApplicationDate(LocalDate.now());
        updateData.setNextDueDate(updateData.getApplicationDate().plusMonths(1));
        updateData.setPet(existingRecord.getPet());
        updateData.setVaccine(existingRecord.getVaccine());

        VaccinationRecordEntity result = vaccinationRecordService.updateVaccinationRecord(existingRecord.getId(), updateData);

        assertNotNull(result);
        assertEquals(existingRecord.getId(), result.getId());
        assertEquals(updateData.getApplicationDate(), result.getApplicationDate());
    }

    @Test
    void testUpdateRecordNotFound() {
        VaccinationRecordEntity updateData = factory.manufacturePojo(VaccinationRecordEntity.class);
        updateData.setApplicationDate(LocalDate.now());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccinationRecordService.updateVaccinationRecord(9999L, updateData));
        assertNotNull(ex);
    }

    @Test
    void testUpdateRecordFutureDateFails() {
        VaccinationRecordEntity existingRecord = recordsData.get(0);

        VaccinationRecordEntity updateData = factory.manufacturePojo(VaccinationRecordEntity.class);
        updateData.setApplicationDate(LocalDate.now().plusMonths(2));
        updateData.setPet(existingRecord.getPet());
        updateData.setVaccine(existingRecord.getVaccine());

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.updateVaccinationRecord(existingRecord.getId(), updateData));
        assertNotNull(ex);
    }

    @Test
    void testUpdateRecordNextDueBeforeApplicationFails() {
        VaccinationRecordEntity existingRecord = recordsData.get(0);

        VaccinationRecordEntity updateData = factory.manufacturePojo(VaccinationRecordEntity.class);
        updateData.setApplicationDate(LocalDate.now());
        updateData.setNextDueDate(LocalDate.now().minusDays(1));
        updateData.setPet(existingRecord.getPet());
        updateData.setVaccine(existingRecord.getVaccine());

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.updateVaccinationRecord(existingRecord.getId(), updateData));
        assertNotNull(ex);
    }

    // ==========================================
    // DELETE
    // ==========================================

    @Test
    void testDeleteRecordSuccess() throws EntityNotFoundException {
        VaccinationRecordEntity existingRecord = recordsData.get(0);
        Long id = existingRecord.getId();

        vaccinationRecordService.deleteVaccinationRecord(id);

        VaccinationRecordEntity deleted = entityManager.find(VaccinationRecordEntity.class, id);
        assertNull(deleted, "The record should have been deleted from the database");
    }

    @Test
    void testDeleteRecordNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccinationRecordService.deleteVaccinationRecord(9999L));
        assertNotNull(ex);
    }
}
