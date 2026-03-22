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

import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VaccinationRecordService.class)
class VaccinationRecordServiceTest {

    @Autowired private VaccinationRecordService service;
    @Autowired private TestEntityManager entityManager;
    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<VaccinationRecordEntity> data = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        entityManager.getEntityManager().createQuery("delete from VaccinationRecordEntity").executeUpdate();
        for (int i = 0; i < 3; i++) {
            VaccinationRecordEntity entity = factory.manufacturePojo(VaccinationRecordEntity.class);
            entity.setApplicationDate(LocalDate.now().minusDays(i));
            entity.setNextDueDate(LocalDate.now().plusMonths(6));
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test void testCreateSuccess() throws IllegalOperationException {
        VaccinationRecordEntity newE = factory.manufacturePojo(VaccinationRecordEntity.class);
        newE.setApplicationDate(LocalDate.now());
        newE.setNextDueDate(LocalDate.now().plusDays(1));
        assertNotNull(service.createVaccinationRecord(newE));
    }

    @Test void testCreateFailDates() {
        VaccinationRecordEntity newE = factory.manufacturePojo(VaccinationRecordEntity.class);
        newE.setApplicationDate(LocalDate.now());
        newE.setNextDueDate(LocalDate.now().minusDays(1));
        assertNotNull(assertThrows(IllegalOperationException.class, () -> service.createVaccinationRecord(newE)));
    }

    @Test void testGetRecords() {
        assertEquals(data.size(), service.getVaccinationRecords().size());
    }

    @Test void testGetOne() throws EntityNotFoundException {
        VaccinationRecordEntity entity = data.get(0);
        assertEquals(entity.getId(), service.getVaccinationRecord(entity.getId()).getId());
    }

    @Test void testUpdate() throws EntityNotFoundException, IllegalOperationException {
        VaccinationRecordEntity entity = data.get(0);
        VaccinationRecordEntity pojo = factory.manufacturePojo(VaccinationRecordEntity.class);
        pojo.setApplicationDate(LocalDate.now());
        pojo.setNextDueDate(LocalDate.now().plusYears(1));
        assertNotNull(service.updateVaccinationRecord(entity.getId(), pojo));
    }

    @Test void testDelete() throws EntityNotFoundException {
        service.deleteVaccinationRecord(data.get(0).getId());
        assertNull(entityManager.find(VaccinationRecordEntity.class, data.get(0).getId()));
    }
}