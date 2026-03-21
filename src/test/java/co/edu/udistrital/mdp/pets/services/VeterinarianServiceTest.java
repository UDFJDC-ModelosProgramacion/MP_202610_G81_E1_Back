package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@EntityScan(basePackages = {
    "co.edu.udistrital.mdp.pets.entities",
    "co.edu.udistrital.mdp.pets.services"
})
@Import(VeterinarianService.class)
class VeterinarianServiceTest {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<VeterinarianEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from MedicalEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VaccinationRecordEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionFollowUpEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            VeterinarianEntity entity = factory.manufacturePojo(VeterinarianEntity.class);
            entity.setEmail("vet" + i + "@refugio.com");
            entity.setPhone("300444555" + i);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

	@Test
	void testCreateVeterinarianSuccess() throws IllegalOperationException {
		VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
		
		// Forzamos datos validos para saltar los validadores de UserService
		newEntity.setEmail("nuevo_vet@test.com");
		newEntity.setPhone("3101234567"); 
		newEntity.setSpecialty("Cirujano");
		newEntity.setAvailability("Turno Mañana");
		newEntity.setName("Dr. Perez");
		newEntity.setPassword("admin123");

		VeterinarianEntity result = (VeterinarianEntity) veterinarianService.createUser(newEntity);

		assertNotNull(result);
		assertEquals("3101234567", result.getPhone());
	}

    @Test
    void testCreateVeterinarianInvalidData() {
        // Test sin especialidad
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
            newEntity.setSpecialty(""); 
            veterinarianService.createUser(newEntity);
        });
    }

    @Test
    void testDeleteVeterinarianSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = data.get(0);
        veterinarianService.deleteUser(entity.getId());
        
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithMedicalEvents() {
        VeterinarianEntity vet = data.get(0);

        // simulamos un evento medico vinculado
        MedicalEventEntity event = factory.manufacturePojo(MedicalEventEntity.class);
        event.setVeterinarian(vet);
        
        vet.getMedicalEvents().add(event);

        entityManager.persist(event);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.deleteUser(vet.getId());
        });
    }

    @Test
    void testDeleteVeterinarianWithFollowUps() {
        VeterinarianEntity vet = data.get(1);

        // simulamos un seguimiento de adopcion vinculado
        AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        
        followUp.setVeterinarian(vet); 
        
		vet.getAdoptionFollowUps().add(followUp);

        entityManager.persist(followUp);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.deleteUser(vet.getId());
        });
    }
}
