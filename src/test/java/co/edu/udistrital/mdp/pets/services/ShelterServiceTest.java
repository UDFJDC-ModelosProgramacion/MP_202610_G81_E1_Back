package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterService.class)
class ShelterServiceTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<ShelterEntity> data = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            ShelterEntity entity = factory.manufacturePojo(ShelterEntity.class);
            // Aseguramos datos únicos para no chocar con las validaciones de creación
            entity.setName("Shelter " + i);
            entity.setEmail("contact" + i + "@test.com");
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test
    void testCreateShelter() throws IllegalOperationException {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setName("New Unique Shelter");
        newEntity.setEmail("unique@mail.com");
        newEntity.setCity("Bogotá");
        newEntity.setGallery("gallery.jpg");

        ShelterEntity result = shelterService.createShelter(newEntity);

        assertNotNull(result);
        ShelterEntity entity = entityManager.find(ShelterEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(newEntity.getEmail(), entity.getEmail());
    }

    @Test
    void testCreateShelterWithSameName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setName(data.get(0).getName()); // Nombre repetido
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithSameEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setEmail(data.get(0).getEmail()); // Email repetido
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testGetShelters() {
        List<ShelterEntity> list = shelterService.getShelters();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetShelter() throws EntityNotFoundException {
        ShelterEntity entity = data.get(0);
        ShelterEntity resultEntity = shelterService.getShelter(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getName(), resultEntity.getName());
    }

    @Test
    void testGetInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.getShelter(999L);
        });
    }

    @Test
    void testUpdateShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = data.get(0);
        ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
        pojoEntity.setName("Updated Name");
        pojoEntity.setEmail("updated@mail.com");
        pojoEntity.setCity("Medellín");
        pojoEntity.setGallery("new_gallery.png");

        ShelterEntity resp = shelterService.updateShelter(entity.getId(), pojoEntity);

        assertNotNull(resp);
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getEmail(), resp.getEmail());
    }

    @Test
    void testDeleteShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = data.get(0);
        shelterService.deleteShelter(entity.getId());
        ShelterEntity deleted = entityManager.find(ShelterEntity.class, entity.getId());
        assertNull(deleted);
    }

	@Test
		void testDeleteShelterWithPets() {
			assertThrows(IllegalOperationException.class, () -> {
				ShelterEntity shelter = data.get(0);
				PetEntity pet = factory.manufacturePojo(PetEntity.class);
				pet.setShelter(shelter);
				entityManager.persist(pet);
				
				shelter.getPets().add(pet); 
				entityManager.persist(pet);
        		entityManager.flush(); //  sincronizar el estado 
				shelterService.deleteShelter(shelter.getId());
			});
	}

	@Test
    void testDeleteShelterWithActiveEvents() {
        // crear el shelter con Podam
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        // crear un evento y forzar el estado "No finalizado"
        ShelterEventEntity event = factory.manufacturePojo(ShelterEventEntity.class);
        event.setStatus(ProcessStatus.IN_PROGRESS); 
        event.setShelter(shelter); // Relación bidireccional
        
        // agregamos el evento a la lista del shelter para que el stream en el Service lo encuentre
        shelter.getEvents().add(event);
        
        entityManager.persist(event);
        
        // sincronizamos con la base de datos H2
        entityManager.flush();
		entityManager.flush();
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.deleteShelter(shelter.getId());
        });
    }

    // --- Additional Tests for validateData (via createShelter and updateShelter) ---
    @Test
    void testCreateShelterWithNullShelterEntity() {
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(null);
        });
    }

    @Test
    void testCreateShelterWithNullName() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setName(null);
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithEmptyName() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setName("   ");
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithNullEmail() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setEmail(null);
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithEmptyEmail() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setEmail("   ");
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithNullCity() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setCity(null);
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithEmptyCity() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setCity("   ");
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithNullGallery() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setGallery(null);
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithEmptyGallery() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setGallery("   ");
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.createShelter(newEntity);
        });
    }

    // --- Additional Tests for findSheltersByName ---
    @Test
    void testFindSheltersByNameReturnsResults() {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setName("UniqueFindName");
        newEntity.setEmail("unique.find@test.com");
        newEntity.setCity("FindCity");
        newEntity.setGallery("find_gallery.jpg");
        entityManager.persist(newEntity);
        entityManager.flush();

        List<ShelterEntity> result = shelterService.findSheltersByName("Unique");
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("UniqueFindName", result.get(0).getName());
    }

    @Test
    void testFindSheltersByNameReturnsNoResults() {
        List<ShelterEntity> result = shelterService.findSheltersByName("NonExistentName");
        assertTrue(result.isEmpty());
    }

    // --- Additional Tests for updateShelter ---
    @Test
    void testUpdateShelterNotFound() {
        ShelterEntity updateData = factory.manufacturePojo(ShelterEntity.class);
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.updateShelter(999L, updateData); // ID que no existe
        });
    }

    @Test
    void testUpdateShelterWithExistingName() {
        ShelterEntity existingShelter = data.get(0);
        ShelterEntity anotherShelter = data.get(1); // Get another existing shelter

        ShelterEntity updateData = factory.manufacturePojo(ShelterEntity.class);
        updateData.setName(anotherShelter.getName()); // Set name to an already existing one
        updateData.setEmail(existingShelter.getEmail()); // Keep existing email to avoid email collision
        
        assertThrows(IllegalOperationException.class, () -> {
            shelterService.updateShelter(existingShelter.getId(), updateData);
        });
    }

    @Test
    void testUpdateShelterWithExistingEmail() {
        ShelterEntity existingShelter = data.get(0);
        ShelterEntity anotherShelter = data.get(1); // Get another existing shelter

        ShelterEntity updateData = factory.manufacturePojo(ShelterEntity.class);
        updateData.setEmail(anotherShelter.getEmail()); // Set email to an already existing one
        updateData.setName(existingShelter.getName()); // Keep existing name to avoid name collision

        assertThrows(IllegalOperationException.class, () -> {
            shelterService.updateShelter(existingShelter.getId(), updateData);
        });
    }

    // --- Additional Tests for deleteShelter ---
    @Test
    void testDeleteShelterNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.deleteShelter(999L); // ID que no existe
        });
    }

    @Test
    void testDeleteShelterWithNullPetsSuccess() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = data.get(0);
        shelter.setPets(null); // Set pets to null
        shelter.setEvents(null); // Ensure no events either
        entityManager.persist(shelter);
        entityManager.flush();

        shelterService.deleteShelter(shelter.getId());
        assertNull(entityManager.find(ShelterEntity.class, shelter.getId()));
    }

    @Test
    void testDeleteShelterWithEmptyPetsSuccess() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = data.get(0);
        shelter.setPets(new ArrayList<>()); // Set pets to empty list
        shelter.setEvents(null); // Ensure no events either
        entityManager.persist(shelter);
        entityManager.flush();

        shelterService.deleteShelter(shelter.getId());
        assertNull(entityManager.find(ShelterEntity.class, shelter.getId()));
    }

    @Test
    void testDeleteShelterWithNullEventsSuccess() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = data.get(0);
        shelter.setEvents(null); // Set events to null
        shelter.setPets(null); // Ensure no pets either
        entityManager.persist(shelter);
        entityManager.flush();

        shelterService.deleteShelter(shelter.getId());
        assertNull(entityManager.find(ShelterEntity.class, shelter.getId()));
    }

    @Test
    void testDeleteShelterWithOnlyCompletedEventsSuccess() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = data.get(0);
        shelter.setPets(null); // Ensure no pets
        
        // Create a completed event
        ShelterEventEntity completedEvent = factory.manufacturePojo(ShelterEventEntity.class);
        completedEvent.setStatus(ProcessStatus.COMPLETED);
        completedEvent.setShelter(shelter); 
        
        if (shelter.getEvents() == null) {
            shelter.setEvents(new ArrayList<>());
        }
        shelter.getEvents().add(completedEvent);
        
        entityManager.persist(completedEvent);
        entityManager.persist(shelter);
        entityManager.flush();

        shelterService.deleteShelter(shelter.getId());
        assertNull(entityManager.find(ShelterEntity.class, shelter.getId()));
    }

    @Test
    void testDeleteShelterWithCreatedEventFails() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity shelter = data.get(0);
            shelter.setPets(null);
            
            ShelterEventEntity createdEvent = factory.manufacturePojo(ShelterEventEntity.class);
            createdEvent.setStatus(ProcessStatus.CREATED); 
            createdEvent.setShelter(shelter); 
            
            if (shelter.getEvents() == null) {
                shelter.setEvents(new ArrayList<>());
            }
            shelter.getEvents().add(createdEvent);
            
            entityManager.persist(createdEvent);
            entityManager.persist(shelter);
            entityManager.flush();

            shelterService.deleteShelter(shelter.getId());
        });
    }

    @Test
    void testDeleteShelterWithPendingEventFails() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity shelter = data.get(0);
            shelter.setPets(null);
            
            ShelterEventEntity pendingEvent = factory.manufacturePojo(ShelterEventEntity.class);
            pendingEvent.setStatus(ProcessStatus.PENDING); 
            pendingEvent.setShelter(shelter); 
            
            if (shelter.getEvents() == null) {
                shelter.setEvents(new ArrayList<>());
            }
            shelter.getEvents().add(pendingEvent);
            
            entityManager.persist(pendingEvent);
            entityManager.persist(shelter);
            entityManager.flush();

            shelterService.deleteShelter(shelter.getId());
        });
    }

    @Test
    void testUpdateShelterWithUnchangedEmailSuccess() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity existingShelter = data.get(0); // Assuming email is "contact0@test.com"
        Long shelterId = existingShelter.getId();

        ShelterEntity updateData = factory.manufacturePojo(ShelterEntity.class);
        // Keep the email the same as the existing shelter
        updateData.setEmail(existingShelter.getEmail()); 
        
        // Change other mandatory fields to ensure validateData passes
        updateData.setName("Same Email, New Name");
        updateData.setCity("New City");
        updateData.setGallery("new_gallery_for_same_email.jpg");

        ShelterEntity result = shelterService.updateShelter(shelterId, updateData);

        assertNotNull(result);
        assertEquals(shelterId, result.getId());
        assertEquals("Same Email, New Name", result.getName());
        assertEquals(existingShelter.getEmail(), result.getEmail()); // Email should remain unchanged
    }
}
