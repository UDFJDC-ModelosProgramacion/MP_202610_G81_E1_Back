package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
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

import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@DataJpaTest
@Transactional
@Import(MedicalEventService.class)
@EntityScan("co.edu.udistrital.mdp.pets.entities") // Explicitly scan for entities
class MedicalEventServiceTest {

    @Autowired
    private MedicalEventService medicalEventService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<MedicalEventEntity> data = new ArrayList<>();
    private MedicalHistoryEntity commonHistory;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        data.clear();
        // Persist a valid history
        commonHistory = factory.manufacturePojo(MedicalHistoryEntity.class);
        entityManager.persist(commonHistory);

        for (int i = 0; i < 3; i++) {
            MedicalEventEntity entity = factory.manufacturePojo(MedicalEventEntity.class);
            entity.setEventDate(LocalDate.now().minusDays(i));
            entity.setMedicalHistory(commonHistory);
            entityManager.persist(entity);
            data.add(entity);
        }
        entityManager.flush();
    }

    // ==========================================
    // CREATION
    // ==========================================

    @Test
    void createMedicalEventWithExistingHistorySuccess() throws IllegalOperationException {
        MedicalEventEntity newEntity = factory.manufacturePojo(MedicalEventEntity.class);
        newEntity.setEventDate(LocalDate.now());
        newEntity.setMedicalHistory(commonHistory);

        MedicalEventEntity result = medicalEventService.createMedicalEvent(newEntity);

        assertNotNull(result.getId());
        assertEquals(commonHistory.getId(), result.getMedicalHistory().getId());
    }

    @Test
    void createMedicalEventWithNonexistentHistoryFails() {
        MedicalEventEntity newEntity = factory.manufacturePojo(MedicalEventEntity.class);
        newEntity.setEventDate(LocalDate.now());
        // History with non-persisted id
        MedicalHistoryEntity fakeHistory = new MedicalHistoryEntity();
        fakeHistory.setId(999999L);
        newEntity.setMedicalHistory(fakeHistory);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.createMedicalEvent(newEntity));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("history") || ex.getMessage().toLowerCase().contains("does not exist"));
    }

    @Test
    void createMedicalEventNullMedicalHistoryFails() {
        MedicalEventEntity newEntity = factory.manufacturePojo(MedicalEventEntity.class);
        newEntity.setEventDate(LocalDate.now());
        newEntity.setMedicalHistory(null); // Case: Null medical history

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.createMedicalEvent(newEntity));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("historia") || ex.getMessage().toLowerCase().contains("asociado"));
    }

    @Test
    void createMedicalEventFutureDateFails() {
        MedicalEventEntity newEntity = factory.manufacturePojo(MedicalEventEntity.class);
        newEntity.setEventDate(LocalDate.now().plusDays(10));
        newEntity.setMedicalHistory(commonHistory);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.createMedicalEvent(newEntity));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("future") || ex.getMessage().toLowerCase().contains("date"));
    }

    @Test
    void createMedicalEventNullDateSuccess() throws IllegalOperationException {
        MedicalEventEntity newEntity = factory.manufacturePojo(MedicalEventEntity.class);
        newEntity.setEventDate(null); // Case: Null event date
        newEntity.setMedicalHistory(commonHistory);

        MedicalEventEntity result = medicalEventService.createMedicalEvent(newEntity);

        assertNotNull(result.getId());
        assertEquals(null, result.getEventDate()); // Expect null date to be persisted
        assertEquals(commonHistory.getId(), result.getMedicalHistory().getId());
    }

    @Test
    void createMedicalEventNullFails() {
        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.createMedicalEvent(null));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("null") || ex.getMessage().toLowerCase().contains("cannot be null"));
    }

    // ==========================================
    // READ
    // ==========================================

    @Test
    void getMedicalEventsReturnsAll() {
        List<MedicalEventEntity> events = medicalEventService.getMedicalEvents();
        assertNotNull(events);
        assertTrue(events.size() >= data.size());
    }

    @Test
    void getMedicalEventSuccess() throws EntityNotFoundException {
        MedicalEventEntity existing = data.get(0);
        MedicalEventEntity found = medicalEventService.getMedicalEvent(existing.getId());

        assertNotNull(found);
        assertEquals(existing.getId(), found.getId());
    }

    @Test
    void getMedicalEventNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> medicalEventService.getMedicalEvent(999999L));
        assertNotNull(ex);
        assertNotNull(ex.getMessage());
    }

    // ==========================================
    // UPDATE
    // ==========================================

    @Test
    void updateMedicalEventCannotChangeDate() {
        MedicalEventEntity existing = data.get(0);
        MedicalEventEntity update = factory.manufacturePojo(MedicalEventEntity.class);
        // Try to change the date to a different one
        update.setEventDate(existing.getEventDate().plusDays(1));
        update.setMedicalHistory(commonHistory);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.updateMedicalEvent(existing.getId(), update));
        assertNotNull(ex);
        assertTrue(ex.getMessage().toLowerCase().contains("date") || ex.getMessage().toLowerCase().contains("modify"));
    }

    @Test
    void updateMedicalEventNullDateAllowed() throws EntityNotFoundException, IllegalOperationException {
        MedicalEventEntity existing = data.get(0);
        MedicalEventEntity update = factory.manufacturePojo(MedicalEventEntity.class);
        update.setEventDate(null); // Case: Null event date in update data
        update.setMedicalHistory(commonHistory); // Must provide valid history to pass validateData

        MedicalEventEntity updated = medicalEventService.updateMedicalEvent(existing.getId(), update);

        assertNotNull(updated);
        assertEquals(existing.getId(), updated.getId());
        assertEquals(existing.getEventDate(), updated.getEventDate()); // Original date should be retained
    }

    @Test
    void updateMedicalEventSameDateAllowed() throws EntityNotFoundException, IllegalOperationException {
        MedicalEventEntity existing = data.get(1);
        MedicalEventEntity update = factory.manufacturePojo(MedicalEventEntity.class);
        // Keep the same date
        update.setEventDate(existing.getEventDate());
        update.setMedicalHistory(commonHistory);
        update.setDescription("Updated description");

        MedicalEventEntity updated = medicalEventService.updateMedicalEvent(existing.getId(), update);

        assertNotNull(updated);
        assertEquals(existing.getId(), updated.getId());
        assertEquals(existing.getEventDate(), updated.getEventDate());
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void updateMedicalEventNotFound() {
        MedicalEventEntity update = factory.manufacturePojo(MedicalEventEntity.class);
        update.setEventDate(LocalDate.now());
        update.setMedicalHistory(commonHistory);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> medicalEventService.updateMedicalEvent(123456L, update));
        assertNotNull(ex);
        assertNotNull(ex.getMessage());
    }

    @Test
    void updateMedicalEventWithoutHistoryFails() {
        MedicalEventEntity existing = data.get(0);
        MedicalEventEntity update = factory.manufacturePojo(MedicalEventEntity.class);
        update.setEventDate(existing.getEventDate());
        update.setMedicalHistory(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> medicalEventService.updateMedicalEvent(existing.getId(), update));
        assertNotNull(ex);
    }

    // ==========================================
    // DELETE
    // ==========================================

    @Test
    void deleteMedicalEventSuccess() throws EntityNotFoundException {
        MedicalEventEntity existing = data.get(2);
        medicalEventService.deleteMedicalEvent(existing.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> medicalEventService.getMedicalEvent(existing.getId()));
        assertNotNull(ex);
    }

    @Test
    void deleteMedicalEventNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> medicalEventService.deleteMedicalEvent(888888L));
        assertNotNull(ex);
    }
}