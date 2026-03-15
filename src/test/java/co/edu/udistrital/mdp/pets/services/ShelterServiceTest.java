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
public class ShelterServiceTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<ShelterEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
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
				
				// inicializar la lista si es null antes de agregar
				if (shelter.getPets() == null) {
					shelter.setPets(new ArrayList<>());
				}
				shelter.getPets().add(pet); 
				
				shelterService.deleteShelter(shelter.getId());
			});
	}

	@Test
    void testDeleteShelterWithActiveEvents() {
        // crear el shelter con Podam
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        
        // inicializacion de seguridad para evitar el NullPointerException del List
        if (shelter.getEvents() == null) {
            shelter.setEvents(new java.util.ArrayList<>());
        }
        
        entityManager.persist(shelter);

        // crear un evento y forzar el estado "No finalizado"
        ShelterEventEntity event = factory.manufacturePojo(ShelterEventEntity.class);
        
        // usamos el Enum ProcessStatus 
        event.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.IN_PROGRESS); 
        event.setShelter(shelter); // Relación bidireccional
        
        // agregamos el evento a la lista del shelter para que el stream en el Service lo encuentre
        shelter.getEvents().add(event);
        
        entityManager.persist(event);
        
        // sincronizamos con la base de datos H2
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            shelterService.deleteShelter(shelter.getId());
        });
    }
}
