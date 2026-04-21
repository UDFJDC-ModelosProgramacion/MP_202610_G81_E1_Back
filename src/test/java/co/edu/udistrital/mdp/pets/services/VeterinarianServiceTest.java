package co.edu.udistrital.mdp.pets.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Type;
import java.time.LocalDate;

import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.MedicalEventDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.VaccinationRecordDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
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

	@MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<VeterinarianEntity> data = new ArrayList<>();

    @BeforeEach
    @SuppressWarnings("unused")
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

        AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);

        followUp.setVeterinarian(vet); 

    vet.getAdoptionFollowUps().add(followUp);

        entityManager.persist(followUp);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.deleteUser(vet.getId());
        });
    }

    private static Stream<Arguments> invalidUpdateVeterinarianData() {
        return Stream.of(
            arguments("specialty", null),
            arguments("specialty", ""),
            arguments("specialty", "   "),
            arguments("availability", null),
            arguments("availability", ""),
            arguments("availability", "   ")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateVeterinarianData")
    void testUpdateVeterinarianWithInvalidFields(String field, String invalidValue) {
        VeterinarianEntity entity = data.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setEmail("updated_vet@refugio.com");
        pojoEntity.setPhone("3209876543");
        pojoEntity.setName("Updated Vet Name");
        pojoEntity.setPassword("newpassword");
        pojoEntity.setSpecialty("General"); 
        pojoEntity.setAvailability("Part-time"); 

        if ("specialty".equals(field)) {
            pojoEntity.setSpecialty(invalidValue);
        } else if ("availability".equals(field)) {
            pojoEntity.setAvailability(invalidValue);
        }

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.updateUser(entity.getId(), pojoEntity);
        });
    }
    
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
            veterinarianService.updateUser(999L, pojoEntity); 
        });
    }

    @Test
    void testDeleteVeterinarianNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.deleteUser(999L); 
        });
    }

    @Test
    void testDeleteVeterinarianWithNullAdoptionFollowUpsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setAdoptionFollowUps(null); 
        vet.setMedicalEvents(null); 
        vet.setVaccinationRecords(null); 
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithEmptyAdoptionFollowUpsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setAdoptionFollowUps(new ArrayList<>()); 
        vet.setMedicalEvents(null); 
        vet.setVaccinationRecords(null); 
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithNullMedicalEventsAndVaccinationRecordsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setMedicalEvents(null); 
        vet.setVaccinationRecords(null);
        vet.setAdoptionFollowUps(null);
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithEmptyMedicalEventsAndVaccinationRecordsSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = data.get(0);
        vet.setMedicalEvents(new ArrayList<>());
        vet.setVaccinationRecords(new ArrayList<>());
        vet.setAdoptionFollowUps(null);
        entityManager.persist(vet);
        entityManager.flush();

        veterinarianService.deleteUser(vet.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, vet.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVeterinarianWithVaccinationRecords() {
        VeterinarianEntity vet = data.get(0);

        vet.getVaccinationRecords().add(factory.manufacturePojo(co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity.class));

        entityManager.persist(vet); 
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            veterinarianService.deleteUser(vet.getId());
        });
    }

    @Test
    void testFindAllVets() {
        VeterinarianDTO dto = new VeterinarianDTO();
        Mockito.when(modelMapper.map(Mockito.any(VeterinarianEntity.class), Mockito.eq(VeterinarianDTO.class)))
               .thenReturn(dto);

        List<VeterinarianDTO> result = veterinarianService.findAllVets();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(data.size(), result.size());
    }

    @Test
    void testGetVetDetailSuccess() throws EntityNotFoundException {
        VeterinarianEntity entity = data.get(0);
        VeterinarianDetailDTO detailDTO = new VeterinarianDetailDTO();
        
        Mockito.when(modelMapper.map(entity, VeterinarianDetailDTO.class)).thenReturn(detailDTO);

        VeterinarianDetailDTO result = veterinarianService.getVetDetail(entity.getId());

        assertNotNull(result);
    }

    @Test
    void testGetVetDetailInvalidType() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.getVetDetail(999L); 
        });
    }

    @Test
    void testCreateFromDTOSuccess() throws IllegalOperationException {
        VeterinarianDTO dto = new VeterinarianDTO();
        dto.setName("Dr. Smith");
        dto.setEmail("vet_dto@test.com");
        dto.setPhone("3001112222");
        dto.setPassword("pass123");
        dto.setSpecialty("Cirujano");
        dto.setAvailability("Mañana");

        VeterinarianEntity entity = new VeterinarianEntity();
        entity.setName("Dr. Smith");
        entity.setEmail("vet_dto@test.com");
        entity.setPhone("3001112222");
        entity.setPassword("pass123");
        entity.setSpecialty("Cirujano");
        entity.setAvailability("Mañana");

        Mockito.when(modelMapper.map(dto, VeterinarianEntity.class)).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, VeterinarianDTO.class)).thenReturn(dto);

        VeterinarianDTO result = veterinarianService.createFromDTO(dto);

        assertNotNull(result);
        assertEquals("Dr. Smith", result.getName());
    }

    @Test
    void testUpdateFromDTOSuccess() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity existing = data.get(0);
        
        VeterinarianDTO dto = new VeterinarianDTO();
        dto.setName("Updated Name");
        dto.setEmail("update@vet.com");
        dto.setPhone("3221234567");
        dto.setPassword("newpass123");
        dto.setSpecialty("Neurología");
        dto.setAvailability("Noche");

        VeterinarianEntity updated = new VeterinarianEntity();
        updated.setId(existing.getId());
        updated.setName("Updated Name");
        updated.setEmail("update@vet.com");
        updated.setPhone("3221234567");
        updated.setPassword("newpass123");
        updated.setSpecialty("Neurología");
        updated.setAvailability("Noche");

        Mockito.when(modelMapper.map(dto, VeterinarianEntity.class)).thenReturn(updated);
        Mockito.when(modelMapper.map(updated, VeterinarianDTO.class)).thenReturn(dto);

        VeterinarianDTO result = veterinarianService.updateFromDTO(existing.getId(), dto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("3221234567", result.getPhone());
    }

	@Test
    void testGetVaccinationsEntities() throws EntityNotFoundException {
        VeterinarianEntity vet = data.get(0);
        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        
        LocalDate testDate = LocalDate.of(2026, 4, 21);
        record.setVaccinationDate(testDate);
        record.setVeterinarian(vet);
        
        entityManager.persist(record);
        entityManager.flush();
        entityManager.refresh(vet);

        List<VaccinationRecordEntity> result = veterinarianService.getVaccinationsEntities(vet.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(testDate, result.get(0).getVaccinationDate());
    }

    @Test
    void testGetMedicalEventsEntities() throws EntityNotFoundException {
        VeterinarianEntity vet = data.get(1);
        MedicalEventEntity event = factory.manufacturePojo(MedicalEventEntity.class);
        event.setVeterinarian(vet);
        
        entityManager.persist(event);
        entityManager.flush();
        entityManager.refresh(vet);

        List<MedicalEventEntity> result = veterinarianService.getMedicalEventsEntities(vet.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(event.getDescription(), result.get(0).getDescription());
    }

	@Test
    void testGetFollowUpsEntities() throws EntityNotFoundException {
        VeterinarianEntity vet = data.get(2);
        AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        
        followUp.setNotes("Todo en orden con la mascota");
        followUp.setFrequency("Mensual");
        followUp.setVeterinarian(vet);
        
        entityManager.persist(followUp);
        entityManager.flush();
        entityManager.refresh(vet);

        List<AdoptionFollowUpEntity> result = veterinarianService.getFollowUpsEntities(vet.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Todo en orden con la mascota", result.get(0).getNotes());
        assertEquals("Mensual", result.get(0).getFrequency());
    }

    @Test
    void testGetNotifications() throws EntityNotFoundException {
        VeterinarianEntity vet = data.get(0);
        List<NotificationEntity> result = veterinarianService.getNotifications(vet.getId());

        assertNotNull(result);
    }


}
