package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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







    // --- Additional Tests for updateUser ---
    @Test
    void testUpdateVeterinarianSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = data.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("updated_vet@refugio.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Vet Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setSpecialty("Pediatra");
        pojoEntity.setAvailability("Part-time");

        VeterinarianEntity resp = (VeterinarianEntity) veterinarianService.updateUser(entity.getId(), pojoEntity);

        assertNotNull(resp);
        assertEquals("Pediatra", resp.getSpecialty());
        assertEquals("Part-time", resp.getAvailability());
    }

    @Test
    void testUpdateVeterinarianNotFound() {
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("non_existent@vet.com");
        pojoEntity.setPhone("1111111111");
        pojoEntity.setSpecialty("General");
        pojoEntity.setAvailability("Any");
        
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.updateUser(999L, pojoEntity); // ID que no existe
        });
    }


    
    @Test
    void testUpdateVeterinarianWithEmptySpecialty() {
        VeterinarianEntity entity = data.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("updated_vet@refugio.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Vet Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setSpecialty("   "); // Empty specialty
        pojoEntity.setAvailability("Part-time");

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.updateUser(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateVeterinarianWithNullAvailability() {
        VeterinarianEntity entity = data.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("updated_vet@refugio.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Vet Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setSpecialty("General");
        pojoEntity.setAvailability(null); // Null availability

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.updateUser(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateVeterinarianWithEmptyAvailability() {
        VeterinarianEntity entity = data.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("updated_vet@refugio.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Vet Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setSpecialty("General");
        pojoEntity.setAvailability("   "); // Empty availability

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.updateUser(entity.getId(), pojoEntity);
        });
    }

    // --- Additional Tests for validateDeletion (via deleteUser) ---
    @Test
    void testDeleteVeterinarianNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.deleteUser(999L); // ID que no existe
        });
    }

    @Test
    void testDeleteVeterinarianWithNullAdoptionFollowUpsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setAdoptionFollowUps(null); // Set to null
        vet.setMedicalEvents(null); // Also set to null to isolate test
        vet.setVaccinationRecords(null); // Also set to null to isolate test
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithEmptyAdoptionFollowUpsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setAdoptionFollowUps(new ArrayList<>()); // Set to empty list
        vet.setMedicalEvents(null); // Also set to null to isolate test
        vet.setVaccinationRecords(null); // Also set to null to isolate test
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithNullMedicalEventsAndVaccinationRecordsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setMedicalEvents(null); // Set to null
        vet.setVaccinationRecords(null); // Set to null
        vet.setAdoptionFollowUps(null); // Also set to null to isolate test
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithEmptyMedicalEventsAndVaccinationRecordsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setMedicalEvents(new ArrayList<>()); // Set to empty list
        vet.setVaccinationRecords(new ArrayList<>()); // Set to empty list
        vet.setAdoptionFollowUps(null); // Also set to null to isolate test
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithVaccinationRecords() {
        VeterinarianEntity vet = data.get(0);

        // simulamos un registro de vacunacion vinculado
        // Assuming VaccinationRecordEntity and other necessary entities are defined and can be manufactured
        // For simplicity, directly add to list without full mock setup if not strictly needed by current test setup
        // But for a full test, you'd persist these too.
        vet.getVaccinationRecords().add(factory.manufacturePojo(co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity.class));

        entityManager.persist(vet); // Persist vet after modifying its collections
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.deleteUser(vet.getId());
        });
    }

    private static Stream<Arguments> invalidVeterinarianData() {
        return Stream.of(
            // Create scenarios
            Arguments.of("create", null, "Full time", "test_null_spec_create@vet.com", "1234567890"), // Null specialty on create
            Arguments.of("create", "General", null, "test_null_avail_create@vet.com", "1234567891"), // Null availability on create
            Arguments.of("create", "General", "   ", "test_empty_avail_create@vet.com", "1234567892"), // Empty availability on create
            // Update scenarios
            Arguments.of("update", null, "Part-time", "test_null_spec_update@refugio.com", "3209876543") // Null specialty on update
        );
    }

    @ParameterizedTest(name = "{0} with specialty=''{1}'', availability=''{2}''")
    @MethodSource("invalidVeterinarianData")
    void testInvalidVeterinarianDataThrowsException(String operation, String specialty, String availability, String email, String phone) {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity vetEntity = factory.manufacturePojo(VeterinarianEntity.class);
            vetEntity.setEmail(email);
            vetEntity.setPhone(phone);
            vetEntity.setSpecialty(specialty);
            vetEntity.setAvailability(availability);
            vetEntity.setName("Test Vet");
            vetEntity.setPassword("password");

            if ("create".equals(operation)) {
                veterinarianService.createUser(vetEntity);
            } else { // "update" operation
                // Create a valid existing vet to update
                VeterinarianEntity existingVet = factory.manufacturePojo(VeterinarianEntity.class);
                existingVet.setEmail("existing@vet.com");
                existingVet.setPhone("1112223333");
                existingVet.setSpecialty("General");
                existingVet.setAvailability("Full time");
                existingVet.setName("Existing Vet");
                existingVet.setPassword("password");
                entityManager.persist(existingVet);
                entityManager.flush();

                veterinarianService.updateUser(existingVet.getId(), vetEntity);
            }
        });
    }
}
