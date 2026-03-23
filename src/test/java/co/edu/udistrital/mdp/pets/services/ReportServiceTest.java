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
    void setUp() {
        entityManager.getEntityManager().createQuery("delete from ReportEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Como UserEntity es abstracta, usamos Podam para fabricar una subclase concreta 
     * (ej. AdopterEntity o ShelterEntity) o la que tengas implementada.
     */
    private UserEntity createTestUser(String email) {
        // Ajusta AdopterEntity por cualquier clase concreta que extienda de UserEntity
        UserEntity user = factory.manufacturePojo(co.edu.udistrital.mdp.pets.entities.AdopterEntity.class);
        user.setId(null);
        user.setEmail(email);
        return entityManager.persist(user);
    }

    @Test
    void testCreateReportSuccess() throws IllegalOperationException {
        UserEntity reporter = createTestUser("reporter@test.com");
        UserEntity reported = createTestUser("reported@test.com");
        entityManager.flush();

        ReportEntity report = new ReportEntity();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReason("Comportamiento inapropiado");
        report.setStatus(Status.PENDING);
        report.setGenerateDate(LocalDate.now());

        ReportEntity saved = service.createReport(report);

        assertNotNull(saved.getId());
        assertEquals(Status.PENDING, saved.getStatus());
    }

    @Test
    void testUpdateReportStatusByAdminSuccess() throws EntityNotFoundException, IllegalOperationException {
        UserEntity reporter = createTestUser("rep1@test.com");
        UserEntity reported = createTestUser("rep2@test.com");
        
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
    void testFindByGenerateDate(){
        LocalDate today = LocalDate.now();
        UserEntity reporter = createTestUser("a@test.com");
        UserEntity reported = createTestUser("b@test.com");

        ReportEntity r1 = new ReportEntity();
        r1.setReporter(reporter);
        r1.setReportedUser(reported);
        r1.setReason("Motivo 1");
        r1.setGenerateDate(today);
        r1.setStatus(Status.PENDING);
        entityManager.persist(r1);
        
        entityManager.flush();

        List<ReportEntity> found = service.findByGenerateDate(today);
        assertFalse(found.isEmpty());
        assertEquals("Motivo 1", found.get(0).getReason());
    }
}
