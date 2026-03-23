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

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VaccinationRecordService.class)
class VaccinationRecordServiceTest {

    @Autowired
    private VaccinationRecordService vaccinationRecordService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<VaccinationRecordEntity> data = new ArrayList<>();

    @BeforeEach
    public void setUp() {
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
        data.clear();
        for (int i = 0; i < 3; i++) {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            entityManager.persist(pet);

            VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
            vaccine.setName(vaccine.getName() == null ? "Vacuna" + i : vaccine.getName());
            entityManager.persist(vaccine);

            VaccinationRecordEntity entity = factory.manufacturePojo(VaccinationRecordEntity.class);
            entity.setApplicationDate(LocalDate.now().minusDays(i + 1));
            entity.setNextDueDate(entity.getApplicationDate().plusMonths(1));
            entity.setPet(pet);
            entity.setVaccine(vaccine);

            entityManager.persist(entity);
            data.add(entity);
        }
        entityManager.flush();
    }

    // ==========================================
    // CREACIÓN
    // ==========================================

    @Test
    void testCreateRecordSuccess() throws IllegalOperationException {
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
    void testCreateRecordWithNullDateFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithFutureDateFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(LocalDate.now().plusDays(5));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNextDueBeforeApplicationFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(LocalDate.now());
        record.setNextDueDate(LocalDate.now().minusDays(1)); 

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        record.setPet(pet);

        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        entityManager.persist(vaccine);
        record.setVaccine(vaccine);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNullPetFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(LocalDate.now());
        record.setPet(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNullVaccineFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(LocalDate.now());

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        record.setPet(pet);

        record.setVaccine(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    @Test
    void testCreateRecordWithNonexistentVaccineFails() {
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setApplicationDate(LocalDate.now());

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        record.setPet(pet);

        VaccineEntity fakeVaccine = new VaccineEntity();
        fakeVaccine.setId(999999L);
        record.setVaccine(fakeVaccine);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.createVaccinationRecord(record));
        assertNotNull(ex);
    }

    // ==========================================
    // LECTURA
    // ==========================================

    @Test
    void testGetVaccinationRecords() {
        List<VaccinationRecordEntity> list = vaccinationRecordService.getVaccinationRecords();
        assertNotNull(list);
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetVaccinationRecordSuccess() throws EntityNotFoundException {
        VaccinationRecordEntity expected = data.get(0);
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
    // ACTUALIZACIÓN
    // ==========================================

    @Test
    void testUpdateRecordSuccess() throws EntityNotFoundException, IllegalOperationException {
        VaccinationRecordEntity existingRecord = data.get(0);

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
        VaccinationRecordEntity existingRecord = data.get(0);

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
        VaccinationRecordEntity existingRecord = data.get(0);

        VaccinationRecordEntity updateData = factory.manufacturePojo(VaccinationRecordEntity.class);
        updateData.setApplicationDate(LocalDate.now());
        updateData.setNextDueDate(LocalDate.now().minusDays(1)); // inválida
        updateData.setPet(existingRecord.getPet());
        updateData.setVaccine(existingRecord.getVaccine());

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccinationRecordService.updateVaccinationRecord(existingRecord.getId(), updateData));
        assertNotNull(ex);
    }

    // ==========================================
    // ELIMINACIÓN
    // ==========================================

    @Test
    void testDeleteRecordSuccess() throws EntityNotFoundException {
        VaccinationRecordEntity existingRecord = data.get(0);
        Long id = existingRecord.getId();

        vaccinationRecordService.deleteVaccinationRecord(id);

        VaccinationRecordEntity deleted = entityManager.find(VaccinationRecordEntity.class, id);
        assertNull(deleted, "El registro debió ser eliminado de la base de datos");
    }

    @Test
    void testDeleteRecordNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccinationRecordService.deleteVaccinationRecord(9999L));
        assertNotNull(ex);
    }
}