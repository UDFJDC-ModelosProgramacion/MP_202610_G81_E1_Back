package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Import(MedicalEventService.class)
class MedicalEventServiceTest {

    @Autowired private MedicalEventService service;
    @Autowired private TestEntityManager entityManager;
    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<MedicalEventEntity> data = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        entityManager.getEntityManager().createQuery("delete from MedicalEventEntity").executeUpdate();
        for (int i = 0; i < 3; i++) {
            MedicalEventEntity entity = factory.manufacturePojo(MedicalEventEntity.class);
            entity.setEventDate(LocalDate.now().minusDays(i));
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test void testCreate() throws IllegalOperationException {
        MedicalEventEntity newE = factory.manufacturePojo(MedicalEventEntity.class);
        newE.setEventDate(LocalDate.now());
        MedicalHistoryEntity history = factory.manufacturePojo(MedicalHistoryEntity.class);
        entityManager.persist(history);
        newE.setMedicalHistory(history);
        assertNotNull(service.createMedicalEvent(newE));
    }

    @Test void testCreateFailFuture() {
        MedicalEventEntity newE = factory.manufacturePojo(MedicalEventEntity.class);
        newE.setEventDate(LocalDate.now().plusDays(10));
        IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> service.createMedicalEvent(newE));
        assertNotNull(exception);
    }

    @Test void testGetOne() throws EntityNotFoundException {
        assertEquals(data.get(0).getId(), service.getMedicalEvent(data.get(0).getId()).getId());
    }

    @Test void testDelete() throws EntityNotFoundException {
        service.deleteMedicalEvent(data.get(0).getId());
        assertNull(entityManager.find(MedicalEventEntity.class, data.get(0).getId()));
    }
}