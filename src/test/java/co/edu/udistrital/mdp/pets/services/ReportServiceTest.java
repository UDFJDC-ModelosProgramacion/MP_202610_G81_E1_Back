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
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.ReportEntity.Status;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReportService.class)
@SuppressWarnings("null")
class ReportServiceTest {

    @Autowired
    private ReportService service;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        entityManager.getEntityManager().createQuery("delete from ReportEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
        entityManager.clear();
    }

    @Test
    void testCreateReportSuccess() throws IllegalOperationException {
        UserEntity reporter = factory.manufacturePojo(UserEntity.class);
        UserEntity reported = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(reporter);
        entityManager.persist(reported);

        ReportEntity report = new ReportEntity();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReason("Comportamiento inapropiado");

        ReportEntity saved = service.createReport(report);

        assertNotNull(saved.getId());
        assertEquals(Status.PENDING, saved.getStatus());
        assertNotNull(saved.getGenerateDate());
        assertEquals("Comportamiento inapropiado", saved.getReason());
        assertEquals(reported.getId(), saved.getReportedUser().getId());
    }

    @Test
    void testCreateReportWithoutReportedUserFails() {
        ReportEntity report = factory.manufacturePojo(ReportEntity.class);
        report.setReportedUser(null);
        report.setReason("Motivo válido");

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createReport(report));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.REPORT_REPORTED_USER_REQUIRED));
    }

    @Test
    void testCreateReportWithEmptyReasonFails() {
        UserEntity reported = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(reported);

        ReportEntity report = factory.manufacturePojo(ReportEntity.class);
        report.setReportedUser(reported);
        report.setReason("   "); // vacío

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createReport(report));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.REPORT_REASON_EMPTY));
    }

    @Test
    void testUpdateReportStatusByAdminSuccess() throws EntityNotFoundException, IllegalOperationException {
        UserEntity reporter = factory.manufacturePojo(UserEntity.class);
        UserEntity reported = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(reporter);
        entityManager.persist(reported);

        ReportEntity report = new ReportEntity();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReason("Spam");
        report.setGenerateDate(LocalDate.now());
        report.setStatus(Status.PENDING);
        entityManager.persist(report);
        entityManager.flush();

        ReportEntity updated = service.updateReportStatus(report.getId(), Status.REVIEWED, true);
        assertEquals(Status.REVIEWED, updated.getStatus());
    }

    @Test
    void testUpdateReportStatusByNonAdminFails() {
        UserEntity reporter = factory.manufacturePojo(UserEntity.class);
        UserEntity reported = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(reporter);
        entityManager.persist(reported);

        ReportEntity report = new ReportEntity();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReason("Spam");
        report.setGenerateDate(LocalDate.now());
        report.setStatus(Status.PENDING);
        entityManager.persist(report);
        entityManager.flush();

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.updateReportStatus(report.getId(), Status.RESOLVED, false));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.REPORT_PERMISSION_DENIED));
    }

    @Test
    void testGetReportNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getReport(999999L));
        assertNotNull(ex);
    }

    @Test
    void testFindByGenerateDate() throws IllegalOperationException {
        LocalDate today = LocalDate.now();

        UserEntity reporter = factory.manufacturePojo(UserEntity.class);
        UserEntity reported = factory.manufacturePojo(UserEntity.class);
        entityManager.persist(reporter);
        entityManager.persist(reported);

        ReportEntity r1 = new ReportEntity();
        r1.setReporter(reporter);
        r1.setReportedUser(reported);
        r1.setReason("Motivo 1");
        r1.setGenerateDate(today);
        r1.setStatus(Status.PENDING);
        entityManager.persist(r1);

        ReportEntity r2 = new ReportEntity();
        r2.setReporter(reporter);
        r2.setReportedUser(reported);
        r2.setReason("Motivo 2");
        r2.setGenerateDate(today.minusDays(1));
        r2.setStatus(Status.PENDING);
        entityManager.persist(r2);

        entityManager.flush();

        List<ReportEntity> found = service.findByGenerateDate(today);
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals("Motivo 1", found.get(0).getReason());
    }
}
