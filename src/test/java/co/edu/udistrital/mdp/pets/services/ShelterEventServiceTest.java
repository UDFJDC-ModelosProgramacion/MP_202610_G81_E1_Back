package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals; // Added import
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows; // Added import
import org.junit.jupiter.api.BeforeEach; // Added import
import org.junit.jupiter.api.Test; // Added import
import org.junit.jupiter.params.ParameterizedTest; // Added import
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterEventService.class)
@EntityScan("co.edu.udistrital.mdp.pets.entities") // Explicitly scan for entities
class ShelterEventServiceTest {

    @Autowired
    private ShelterEventService shelterEventService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<ShelterEventEntity> data = new ArrayList<>();
    private ShelterEntity shelter;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ShelterEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        // 1. Crear el refugio dueño de los eventos
        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        // 2. Crear eventos de prueba
        for (int i = 0; i < 3; i++) {
            ShelterEventEntity entity = factory.manufacturePojo(ShelterEventEntity.class);
            entity.setShelter(shelter);
            entity.setStatus(ProcessStatus.IN_PROGRESS);
            entity.setDate(LocalDate.now());
            
            entityManager.persist(entity);
            data.add(entity);
        }
        entityManager.flush();
    }

    // --- TESTS DE CREACIÓN ---

    @Test
    void createShelterEventSuccessTest() throws IllegalOperationException {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        newEvent.setTitle("Feria de Adopción");
        newEvent.setDate(LocalDate.now().plusDays(7));
        newEvent.setLocation("Parque Simón Bolívar");
        newEvent.setShelter(shelter);

        ShelterEventEntity result = shelterEventService.createShelterEvent(newEvent);
        
        assertNotNull(result);
        assertEquals(ProcessStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Feria de Adopción", result.getTitle());
    }

    @Test
    void createShelterEventNullTest() {
        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(null));
    }

    // Parameterized test for null/empty title and location
    @ParameterizedTest
    @MethodSource("invalidTitleAndLocationProvider")
    void createShelterEventInvalidTitleOrLocationTest(String title, String location) {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        newEvent.setTitle(title);
        newEvent.setDate(LocalDate.now().plusDays(7));
        newEvent.setLocation(location);
        newEvent.setShelter(shelter);

        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(newEvent));
    }

    private static Stream<Arguments> invalidTitleAndLocationProvider() {
        return Stream.of(
            // Null Title scenarios
            arguments(null, "Parque Simón Bolívar"),
            // Empty Title scenarios
            arguments("   ", "Parque Simón Bolívar"),
            // Null Location scenarios
            arguments("Valid Title", null),
            // Empty Location scenarios
            arguments("Valid Title", "   ")
        );
    }
    @Test
    void createShelterEventNullDateTest() {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        newEvent.setTitle("Valid Title");
        newEvent.setDate(null); // Case: Null date
        newEvent.setLocation("Valid Location");
        newEvent.setShelter(shelter);

        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(newEvent));
    }

    @Test
    void createShelterEventNullShelterTest() {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        newEvent.setTitle("Valid Title");
        newEvent.setDate(LocalDate.now().plusDays(7));
        newEvent.setLocation("Valid Location");
        newEvent.setShelter(null); // Case: Null shelter

        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(newEvent));
    }

    @Test
    void createShelterEventShelterIdNullTest() {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        ShelterEntity shelterWithoutId = factory.manufacturePojo(ShelterEntity.class);
        shelterWithoutId.setId(null); // Case: Shelter with null ID
        newEvent.setShelter(shelterWithoutId);
        newEvent.setTitle("Valid Title");
        newEvent.setDate(LocalDate.now().plusDays(7));
        newEvent.setLocation("Valid Location");

        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(newEvent));
    }

    @Test
    void createEventInvalidShelterTest() {
        ShelterEventEntity newEvent = factory.manufacturePojo(ShelterEventEntity.class);
        ShelterEntity nonExistentShelter = new ShelterEntity(); // Corrected instantiation
        nonExistentShelter.setId(999L);
        newEvent.setShelter(nonExistentShelter);

        assertThrows(IllegalOperationException.class, () -> shelterEventService.createShelterEvent(newEvent));
    }

    @Test
    void createShelterEventNullStatusTest() throws IllegalOperationException {
        ShelterEventEntity newEvent = new ShelterEventEntity();
        newEvent.setTitle("Event with null status");
        newEvent.setDate(LocalDate.now().plusDays(10));
        newEvent.setLocation("New Location");
        newEvent.setShelter(shelter);
        newEvent.setStatus(null); // Case: Null status

        ShelterEventEntity result = shelterEventService.createShelterEvent(newEvent);
        
        assertNotNull(result);
        assertEquals(ProcessStatus.IN_PROGRESS, result.getStatus());
    }

    // --- TESTS DE ACTUALIZACIÓN & REGLAS DE NEGOCIO ---

    @Test
    void updateShelterEventSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity existing = data.get(0);
        ShelterEventEntity updateData = new ShelterEventEntity();
        updateData.setTitle("Nuevo Título");
        updateData.setLocation("Nueva Ubicación");
        updateData.setDate(LocalDate.now());
        updateData.setShelter(shelter);

        ShelterEventEntity result = shelterEventService.updateShelterEvent(existing.getId(), updateData);
        assertEquals("Nuevo Título", result.getTitle());
    }
    @Test
    void updateCompletedEventTest() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = data.get(0);
        // Finalizamos el evento primero
        shelterEventService.finishEvent(event.getId());

        ShelterEventEntity updateData = factory.manufacturePojo(ShelterEventEntity.class);
        updateData.setShelter(shelter);

        // No debería dejar editar un evento ya COMPLETED
        assertThrows(IllegalOperationException.class, () -> 
            shelterEventService.updateShelterEvent(event.getId(), updateData)
        );
    }

    // --- TESTS DE FLUJO DE ESTADO (FINISH) ---

    @Test
    void finishEventTest() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = data.get(0);
        ShelterEventEntity result = shelterEventService.finishEvent(event.getId());
        
        assertEquals(ProcessStatus.COMPLETED, result.getStatus());
    }

    @Test
    void finishAlreadyFinishedEventTest() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = data.get(0);
        shelterEventService.finishEvent(event.getId());

        assertThrows(IllegalOperationException.class, () -> shelterEventService.finishEvent(event.getId()));
    }

    // --- TESTS DE BORRADO ---

    @Test
    void deleteShelterEventSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity event = data.get(0);
        // Regla: Solo se borran los terminados
        shelterEventService.finishEvent(event.getId());
        
        shelterEventService.deleteShelterEvent(event.getId());
        
        ShelterEventEntity deleted = entityManager.find(ShelterEventEntity.class, event.getId());
        assertNull(deleted);
    }

    @Test
    void deleteInProgressEventTest() {
        ShelterEventEntity event = data.get(0); // Está IN_PROGRESS por defecto
        
        // Debe fallar según tu regla de negocio
        assertThrows(IllegalOperationException.class, () -> shelterEventService.deleteShelterEvent(event.getId()));
    }

    // --- TESTS DE BÚSQUEDA ---

    @Test
    void getShelterEventsTest() {
        List<ShelterEventEntity> list = shelterEventService.getShelterEvents();
        assertEquals(data.size(), list.size());
    }

    @Test
    void getShelterEventNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> shelterEventService.getShelterEvent(999L));
    }
}
