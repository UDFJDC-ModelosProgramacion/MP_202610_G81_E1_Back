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

import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(TrialCohabitationService.class)
class TrialCohabitationServiceTest {

    @Autowired
    private TrialCohabitationService trialCohabitationService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<TrialCohabitationEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            TrialCohabitationEntity entity = factory.manufacturePojo(TrialCohabitationEntity.class);
            entity.setStartDate(LocalDate.now().minusDays(20 + i));
            entity.setEndDate(LocalDate.now().minusDays(10 + i)); // Ya terminaron
            entity.setResult("EN_PROCESO");
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreateTrialCohabitation() throws IllegalOperationException {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(LocalDate.now());
        newEntity.setEndDate(LocalDate.now().plusDays(14));
        newEntity.setResult("EN_PROCESO");

        TrialCohabitationEntity result = trialCohabitationService.createTrialCohabitation(newEntity);

        assertNotNull(result);
        TrialCohabitationEntity entity = entityManager.find(TrialCohabitationEntity.class, result.getId());
        assertEquals(newEntity.getStartDate(), entity.getStartDate());
    }

    @Test
    void testCreateTrialCohabitationWithNullStartDate() {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(null); // Null - debe fallar
        newEntity.setEndDate(LocalDate.now().plusDays(14));

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.createTrialCohabitation(newEntity);
        });
    }

    @Test
    void testCreateTrialCohabitationWithNullEndDate() {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(LocalDate.now());
        newEntity.setEndDate(null); // Null - debe fallar

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.createTrialCohabitation(newEntity);
        });
    }

    @Test
    void testCreateTrialCohabitationWithEndDateBeforeStartDate() {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(LocalDate.now());
        newEntity.setEndDate(LocalDate.now().minusDays(5)); // Fecha fin antes de inicio

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.createTrialCohabitation(newEntity);
        });
    }

    @Test
    void testCreateTrialCohabitationWithInvalidResult() {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(LocalDate.now());
        newEntity.setEndDate(LocalDate.now().plusDays(14));
        newEntity.setResult("MAS_O_MENOS"); // Valor invalido

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.createTrialCohabitation(newEntity);
        });
    }

    @Test
    void testCreateTrialCohabitationWithValidResult() throws IllegalOperationException {
        TrialCohabitationEntity newEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        newEntity.setStartDate(LocalDate.now());
        newEntity.setEndDate(LocalDate.now().plusDays(14));
        newEntity.setResult("EXITOSA"); // Valor valido

        TrialCohabitationEntity result = trialCohabitationService.createTrialCohabitation(newEntity);
        assertNotNull(result);
    }

    @Test
    void testCreateTrialCohabitationWithNullEntity() {
        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.createTrialCohabitation(null);
        });
    }

	@Test
	void testCreateWithInvalidResult() {
		TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
		trial.setStartDate(LocalDate.now());
		trial.setEndDate(LocalDate.now().plusDays(1));
		trial.setResult("ESTADO_INVENTADO");

		assertThrows(IllegalOperationException.class, () -> 
			trialCohabitationService.createTrialCohabitation(trial)); // <-- Corregido
	}


    // ==================== GET TESTS ====================

    @Test
    void testGetTrialCohabitations() {
        List<TrialCohabitationEntity> list = trialCohabitationService.getTrialCohabitations();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetTrialCohabitation() throws EntityNotFoundException {
        TrialCohabitationEntity entity = data.get(0);
        TrialCohabitationEntity resultEntity = trialCohabitationService.getTrialCohabitation(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getStartDate(), resultEntity.getStartDate());
    }

    @Test
    void testGetInvalidTrialCohabitation() {
        assertThrows(EntityNotFoundException.class, () -> {
            trialCohabitationService.getTrialCohabitation(999L);
        });
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdateTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity entity = data.get(0);
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setStartDate(LocalDate.now());
        pojoEntity.setEndDate(LocalDate.now().plusDays(14));
        pojoEntity.setResult("EXITOSA"); // Cambio valido de EN_PROCESO a EXITOSA

        TrialCohabitationEntity resp = trialCohabitationService.updateTrialCohabitation(entity.getId(), pojoEntity);

        assertNotNull(resp);
    }

    @Test
    void testUpdateTrialCohabitationCannotRevertResult() {
        // Crear una convivencia con resultado final
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(LocalDate.now().minusDays(20));
        trial.setEndDate(LocalDate.now().minusDays(10));
        trial.setResult("EXITOSA"); // Ya tiene resultado final
        entityManager.persist(trial);
        entityManager.flush();

        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setResult("EN_PROCESO"); // Intentando revertir

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.updateTrialCohabitation(trial.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateTrialCohabitationWithInvalidResult() {
        TrialCohabitationEntity entity = data.get(0);
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setResult("INVALIDO");

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.updateTrialCohabitation(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidTrialCohabitation() {
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setResult("EXITOSA");

        assertThrows(EntityNotFoundException.class, () -> {
            trialCohabitationService.updateTrialCohabitation(999L, pojoEntity);
        });
    }

    @Test
    void testUpdateTrialCohabitationWithNullResultInPojo() throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity entity = data.get(0); // This entity has result "EN_PROCESO"
        String originalResult = entity.getResult();
        
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setResult(null); // Set result to null in the POJO
        pojoEntity.setStartDate(entity.getStartDate()); // Keep original start date
        pojoEntity.setEndDate(entity.getEndDate()); // Keep original end date

        TrialCohabitationEntity resp = trialCohabitationService.updateTrialCohabitation(entity.getId(), pojoEntity);

        assertNotNull(resp);
        assertEquals(originalResult, resp.getResult()); // Result should remain unchanged
    }

    @Test
    void testUpdateTrialCohabitationWithNullCurrentResultToValidResult() throws EntityNotFoundException, IllegalOperationException {
        // 1. Create a TrialCohabitationEntity with result = null and persist it
        TrialCohabitationEntity trialWithNullResult = factory.manufacturePojo(TrialCohabitationEntity.class);
        trialWithNullResult.setStartDate(LocalDate.now().minusDays(10));
        trialWithNullResult.setEndDate(LocalDate.now().plusDays(10));
        trialWithNullResult.setResult(null); // Explicitly set to null
        entityManager.persist(trialWithNullResult);
        entityManager.flush(); // Ensure it's persisted

        // 2. Create a pojoEntity with a valid newResult
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setStartDate(trialWithNullResult.getStartDate());
        pojoEntity.setEndDate(trialWithNullResult.getEndDate());
        pojoEntity.setResult("EXITOSA"); // New valid result

        // 3. Call updateTrialCohabitation
        TrialCohabitationEntity updatedTrial = trialCohabitationService.updateTrialCohabitation(
            trialWithNullResult.getId(), pojoEntity);

        // 4. Assert that the update is successful and the result is changed to the newResult
        assertNotNull(updatedTrial);
        assertEquals("EXITOSA", updatedTrial.getResult());
    }

    @Test
    void testUpdateTrialCohabitationWithExistingNullDates() {
        // Simular datos corruptos en BD donde las fechas son nulas
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(null);
        trial.setEndDate(null);
        trial.setResult("EN_PROCESO");
        entityManager.persist(trial);
        entityManager.flush();

        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setStartDate(null); // No actualizar start date
        pojoEntity.setEndDate(null);   // No actualizar end date
        pojoEntity.setResult("EXITOSA");

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.updateTrialCohabitation(trial.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateTrialCohabitationWithEndDateBeforeStartDate() {
        TrialCohabitationEntity entity = data.get(0); // Start: now-20, End: now-10
        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        
        // Actualizamos start date para que sea POSTERIOR a la end date existente
        pojoEntity.setStartDate(LocalDate.now()); 
        pojoEntity.setEndDate(null); // Mantiene end date original (hace 10 dias)

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.updateTrialCohabitation(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateTrialCohabitationIdempotentResult() throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(LocalDate.now().minusDays(20));
        trial.setEndDate(LocalDate.now().minusDays(10));
        trial.setResult("EXITOSA");
        entityManager.persist(trial);
        entityManager.flush();

        TrialCohabitationEntity pojoEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
        pojoEntity.setResult("EXITOSA"); // Mismo resultado
        pojoEntity.setStartDate(trial.getStartDate());
        pojoEntity.setEndDate(trial.getEndDate());

        TrialCohabitationEntity resp = trialCohabitationService.updateTrialCohabitation(trial.getId(), pojoEntity);
        assertNotNull(resp);
        assertEquals("EXITOSA", resp.getResult());
    }

	@Test
	void testUpdateWithEndDateNull() throws EntityNotFoundException, IllegalOperationException {
		TrialCohabitationEntity existing = factory.manufacturePojo(TrialCohabitationEntity.class);
		LocalDate originalEndDate = LocalDate.now().plusDays(10);
		existing.setStartDate(LocalDate.now());
		existing.setEndDate(originalEndDate);
		entityManager.persist(existing);
		
		// intentamos actualizar con un null en la fecha de fin
		TrialCohabitationEntity updateData = new TrialCohabitationEntity();
		updateData.setEndDate(null); 
		
		TrialCohabitationEntity result = trialCohabitationService.updateTrialCohabitation(existing.getId(), updateData);

		assertNotNull(result);
		assertEquals(originalEndDate, result.getEndDate(), "The end date should remain unchanged when providing a null value in update");
	}
    
	@Test
	void testUpdateEndDateBeforeStartDate() {
		// 1. Datos de prueba
		TrialCohabitationEntity existing = new TrialCohabitationEntity();
		existing.setStartDate(LocalDate.of(2026, 3, 1));
		existing.setEndDate(LocalDate.of(2026, 3, 10));
		existing.setResult("EN_PROCESO");
		entityManager.persist(existing);

		TrialCohabitationEntity updateData = new TrialCohabitationEntity();
		updateData.setEndDate(LocalDate.of(2026, 2, 28)); // ERROR: Antes del inicio

		// 2. Usamos la excepción del profe para validar
		assertThrows(IllegalOperationException.class, () -> {
			trialCohabitationService.updateTrialCohabitation(existing.getId(), updateData);
		});
	}
	// ==================== DELETE TESTS ====================

    @Test
    void testDeleteFutureTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(LocalDate.now().plusDays(10));
        trial.setEndDate(LocalDate.now().plusDays(20));
        trial.setResult("EN_PROCESO");
        entityManager.persist(trial);
        entityManager.flush();

        trialCohabitationService.deleteTrialCohabitation(trial.getId());
        assertNull(entityManager.find(TrialCohabitationEntity.class, trial.getId()));
    }

    @Test
    void testDeletePastTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(LocalDate.now().minusDays(20));
        trial.setEndDate(LocalDate.now().minusDays(10));
        trial.setResult("EXITOSA");
        entityManager.persist(trial);
        entityManager.flush();

        trialCohabitationService.deleteTrialCohabitation(trial.getId());
        assertNull(entityManager.find(TrialCohabitationEntity.class, trial.getId()));
    }

    @Test
    void testDeleteTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
        // data ya tiene convivencias terminadas
        TrialCohabitationEntity entity = data.get(0);
        trialCohabitationService.deleteTrialCohabitation(entity.getId());
        TrialCohabitationEntity deleted = entityManager.find(TrialCohabitationEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidTrialCohabitation() {
        assertThrows(EntityNotFoundException.class, () -> {
            trialCohabitationService.deleteTrialCohabitation(999L);
        });
    }

    @Test
    void testDeleteTrialCohabitationInProgress() {
        // Crear una convivencia en curso
        TrialCohabitationEntity trial = factory.manufacturePojo(TrialCohabitationEntity.class);
        trial.setStartDate(LocalDate.now().minusDays(5));
        trial.setEndDate(LocalDate.now().plusDays(5)); // Aun no termina
        trial.setResult("EN_PROCESO");
        entityManager.persist(trial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            trialCohabitationService.deleteTrialCohabitation(trial.getId());
        });
    }

	@Test
	void testDeleteTrialInProgress() {
		TrialCohabitationEntity trial = new TrialCohabitationEntity();
		trial.setStartDate(LocalDate.now().minusDays(1));
		trial.setEndDate(LocalDate.now().plusDays(1));
		entityManager.persist(trial);

		assertThrows(IllegalOperationException.class, () -> 
			trialCohabitationService.deleteTrialCohabitation(trial.getId())); // <-- Corregido
	}
}
