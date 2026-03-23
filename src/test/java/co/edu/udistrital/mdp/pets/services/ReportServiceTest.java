package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.*;
import co.edu.udistrital.mdp.pets.entities.ReportEntity.Status;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Import(ReportService.class)
class ReportServiceTest {

    @Autowired private ReportService service;
    @Autowired private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("delete from ReportEntity").executeUpdate();
        entityManager.flush();
    }

    private UserEntity createTestUser(String email) {
        UserEntity user = factory.manufacturePojo(AdopterEntity.class);
        user.setId(null);
        user.setEmail(email);
        return entityManager.persist(user);
    }

    @Test
    void testCreateReportWithStrategy() throws IllegalOperationException {
        UserEntity reported = createTestUser("target@mail.com");
        MedicalEventReportStrategyEntity strategy = new MedicalEventReportStrategyEntity();
        entityManager.persist(strategy);

        ReportEntity report = new ReportEntity();
        report.setReportedUser(reported);
        report.setReason("Urgencia médica");
        report.setReportStrategy(strategy);

        ReportEntity saved = service.createReport(report);
        
        assertNotNull(saved.getId());
        assertEquals(Status.PENDING, saved.getStatus());
        assertNotNull(saved.getGenerateDate());
        assertEquals(strategy.getId(), saved.getReportStrategy().getId());
    }

    @Test
    void testAssignStrategySuccess() throws EntityNotFoundException {
        // Crear reporte sin estrategia
        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("test@mail.com"));
        report.setReason("Sin estrategia inicial");
        report = entityManager.persist(report);

        // Crear estrategia
        AdoptionReportStrategyEntity strategy = new AdoptionReportStrategyEntity();
        entityManager.persist(strategy);
        entityManager.flush();

        // Asignar
        ReportEntity updated = service.assignStrategy(report.getId(), strategy.getId());
        
        assertNotNull(updated.getReportStrategy());
        assertEquals(strategy.getId(), updated.getReportStrategy().getId());
    }

    @Test
    void testUpdateStatusByAdmin() throws Exception {
        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("admin_test@mail.com"));
        report.setReason("Spam");
        report = entityManager.persist(report);

        ReportEntity updated = service.updateReportStatus(report.getId(), Status.RESOLVED, true);
        assertEquals(Status.RESOLVED, updated.getStatus());
    }

    @Test
    void testUpdateStatusNotAdminThrows() {
        assertThrows(IllegalOperationException.class, () -> 
            service.updateReportStatus(1L, Status.REVIEWED, false));
    }

    @Test
    void testDeleteReport() throws EntityNotFoundException {
        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("del@mail.com"));
        report.setReason("Eliminar");
        report = entityManager.persist(report);
        Long id = report.getId();

        service.deleteReport(id);
        
        assertThrows(EntityNotFoundException.class, () -> service.getReport(id));
    }

    @Test
    void testValidateForCreateExceptions() {
        // Caso Reporte Nulo
        assertThrows(IllegalOperationException.class, () -> service.createReport(null));

        // Caso Sin Reported User
        ReportEntity noUser = new ReportEntity();
        noUser.setReason("Cualquier cosa");
        assertThrows(IllegalOperationException.class, () -> service.createReport(noUser));

        // Caso Razón Vacía
        ReportEntity noReason = new ReportEntity();
        noReason.setReportedUser(createTestUser("empty@mail.com"));
        noReason.setReason("   ");
        assertThrows(IllegalOperationException.class, () -> service.createReport(noReason));
    }

	@Test
    void testGetReportNotFoundThrows() {
        assertThrows(EntityNotFoundException.class, () -> service.getReport(999L));
    }

    @Test
    void testUpdateReportStatusNotFoundThrows() {
        // Admin es true, pero el reporte no existe
        assertThrows(EntityNotFoundException.class, () -> 
            service.updateReportStatus(999L, Status.RESOLVED, true));
    }

    @Test
    void testAssignStrategyReportNotFoundThrows() {
        assertThrows(EntityNotFoundException.class, () -> 
            service.assignStrategy(999L, 1L));
    }

    @Test
	void testAssignStrategyNotFoundThrows() {
		// 1. Preparamos el escenario
		ReportEntity report = new ReportEntity();
		report.setReportedUser(createTestUser("notstrat@test.com"));
		report.setReason("Test");
		report = entityManager.persist(report);
		
		// 2. Extraemos el ID para la lambda
		final Long reportId = report.getId();
		final Long nonExistentStrategyId = 999L;

		// 3. Verificamos que lance la excepción esperada
		assertThrows(EntityNotFoundException.class, () -> 
			service.assignStrategy(reportId, nonExistentStrategyId));
	}

    @Test
    void testDeleteReportNotFoundThrows() {
        assertThrows(EntityNotFoundException.class, () -> service.deleteReport(999L));
    }

	@Test
    void testGetReportsEmptyAndFull() {
        // Caso vacío
        assertTrue(service.getReports().isEmpty());

        // Caso con datos
        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("list1@test.com"));
        report.setReason("Razón 1");
        entityManager.persist(report);
        
        assertFalse(service.getReports().isEmpty());
        assertEquals(1, service.getReports().size());
    }

    @Test
    void testFindByGenerateDate() {
        LocalDate date = LocalDate.now();
        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("date@test.com"));
        report.setReason("Test Date");
        report.setGenerateDate(date);
        entityManager.persist(report);

        List<ReportEntity> results = service.findByGenerateDate(date);
        assertFalse(results.isEmpty());
        assertEquals(date, results.get(0).getGenerateDate());
    }

    @Test
    void testFindByReportStrategy() {
        ReturnReportStrategyEntity strategy = new ReturnReportStrategyEntity();
        entityManager.persist(strategy);

        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("strat_find@test.com"));
        report.setReason("Test Strategy");
        report.setReportStrategy(strategy);
        entityManager.persist(report);

        List<ReportEntity> results = service.findByReportStrategy(strategy);
        assertFalse(results.isEmpty());
        assertEquals(strategy.getId(), results.get(0).getReportStrategy().getId());
    }

	@Test
    void testCreateReportWithReturnStrategy() throws IllegalOperationException {
        ReturnReportStrategyEntity strategy = new ReturnReportStrategyEntity();
        entityManager.persist(strategy);

        ReportEntity report = new ReportEntity();
        report.setReportedUser(createTestUser("return@test.com"));
        report.setReason("Devolución de mascota");
        report.setReportStrategy(strategy);

        ReportEntity saved = service.createReport(report);
        assertNotNull(saved.getReportStrategy());
        // Aquí verificas que el polimorfismo funcionó
        assertTrue(saved.getReportStrategy() instanceof ReturnReportStrategyEntity);
    }
}
