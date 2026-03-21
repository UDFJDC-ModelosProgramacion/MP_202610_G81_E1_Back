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

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(PetService.class)
class PetServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<PetEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from MedicalHistoryEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PetEntity entity = factory.manufacturePojo(PetEntity.class);
            entity.setStatus(PetStatus.AVAILABLE);
            entity.setAge(i + 1); // Edad > 0
            entityManager.persist(entity);
            data.add(entity);
        }
    }

	// TEST de validacion de campos obligatorios
	@Test
	void testCreatePetWithNullName() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setName(null); // Dispara el isBlank
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptySpecies() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSpecies("   "); // Dispara el isBlank con espacios
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}


	// TEST de edad
	@Test
	void testCreatePetWithNullAge() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setAge(null); // Rama 1 del ||
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNegativeAge() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setAge(-5); // Rama 2 del ||
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}


	// TEST de booleanos
	@Test
	void testCreatePetWithNullGoodWithKids() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setGoodWithKids(null); 
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNullGoodWithPets() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setGoodWithPets(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}


	@Test
	void testCreatePetSuccess() throws IllegalOperationException {
		// 1. Fabricamos el objeto base
		PetEntity newPet = factory.manufacturePojo(PetEntity.class);
		
		// FORZAMOS valores validos en los campos que la validacion revisa
		// Esto garantiza que NO entre en ninguno de los 'throw new IllegalOperationException'
		newPet.setName("Firulais");
		newPet.setSpecies("Canino");
		newPet.setBreed("Criollo");
		newPet.setSex("Macho");
		newPet.setSize("Mediano");
		newPet.setAge(3); // Edad > 0
		newPet.setOrigin("Rescate en vía pública");
		newPet.setSpaceRequired("Apartamento o casa");
		newPet.setGoodWithKids(true);
		newPet.setGoodWithPets(true);
		newPet.setStatus(PetStatus.AVAILABLE);

		PetEntity result = petService.createPet(newPet);

		assertNotNull(result);
		assertNotNull(result.getMedicalHistory()); 
		assertEquals(result, result.getMedicalHistory().getPet());
		assertEquals("Firulais", result.getName());
	}

	@Test
	void testUpdatePetSameStatus() throws EntityNotFoundException, IllegalOperationException {
		PetEntity existingPet = data.get(0); // Supongamos que es AVAILABLE
		
		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(existingPet.getStatus()); // Forzamos mismo estado
		updateData.setAge(existingPet.getAge());
		
		// Al ser el mismo estado, el método validateStatusChange hace un "return"
		// y el proceso continúa sin lanzar excepción.
		PetEntity result = petService.updatePet(existingPet.getId(), updateData);
		
		assertNotNull(result);
		assertEquals(existingPet.getStatus(), result.getStatus());
	}

	@Test
	void testUpdateStatusFromAdoptedFails() {
		PetEntity pet = data.get(0);
		pet.setStatus(PetStatus.ADOPTED); // La marcamos como adoptada en la BD
		entityManager.persist(pet);
		entityManager.flush();

		assertThrows(IllegalOperationException.class, () -> {
			PetEntity updateData = factory.manufacturePojo(PetEntity.class);
			updateData.setStatus(PetStatus.AVAILABLE); // Intentamos cambiarle el estado
			petService.updatePet(pet.getId(), updateData);
		});
	}

	@Test
	void testUpdateStatusToTrialWithActiveTrialFails() {
		PetEntity pet = data.get(0);
		
		// Creamos un trial activo
		TrialCohabitationEntity activeTrial = new TrialCohabitationEntity();
		activeTrial.setStatus(ProcessStatus.IN_PROGRESS);
		activeTrial.setPet(pet);
		pet.getTrials().add(activeTrial);
		
		entityManager.persist(activeTrial);
		entityManager.flush();

		assertThrows(IllegalOperationException.class, () -> {
			PetEntity updateData = factory.manufacturePojo(PetEntity.class);
			updateData.setStatus(PetStatus.IN_TRIAL); // Intento de duplicar prueba activa
			petService.updatePet(pet.getId(), updateData);
		});
	}

	@Test
	void testUpdateStatusToAdoptedFromInvalidStatusFails() {
		PetEntity pet = data.get(0);
		pet.setStatus(PetStatus.MEDICAL_TREATMENT); // Estado no apto para adopción directa
		entityManager.persist(pet);
		entityManager.flush();

		assertThrows(IllegalOperationException.class, () -> {
			PetEntity updateData = factory.manufacturePojo(PetEntity.class);
			updateData.setStatus(PetStatus.ADOPTED); 
			petService.updatePet(pet.getId(), updateData);
		});
	}

	@Test
	void testUpdateStatusToAdoptedSuccess() throws EntityNotFoundException, IllegalOperationException {
		PetEntity pet = data.get(1); // Esta AVAILABLE por defecto en tu insertData
		
		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.ADOPTED); // Salto válido: AVAILABLE -> ADOPTED
		updateData.setAge(pet.getAge());
		
		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.ADOPTED, result.getStatus());
	}

	@Test
	void testCreatePetWithNullNameIsBlank() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity pet = factory.manufacturePojo(PetEntity.class);
			pet.setName(null); // Esto obliga a isBlank a retornar TRUE en (str == null)
			petService.createPet(pet);
		});
	}

	@Test
	void testCreatePetWithExistingMedicalHistory() throws IllegalOperationException {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setAge(1);
		pet.setStatus(PetStatus.AVAILABLE);
		// ... asegúrate de que los strings obligatorios tengan algo (Podam suele llenarlos)
		
		// Creamos un historial manualmente y se lo clavamos a la mascota
		MedicalHistoryEntity existingHistory = 
			new MedicalHistoryEntity();
		existingHistory.setDescription("Historial previo de vacunas");
		pet.setMedicalHistory(existingHistory);
		existingHistory.setPet(pet); // Relación bidireccional

		// Ejecutamos
		PetEntity result = petService.createPet(pet);

		// Verificamos que NO se creó uno nuevo (assertSame comprueba que sea la misma instancia de memoria)
		assertSame(existingHistory, result.getMedicalHistory(), 
			"No se debería haber creado un historial nuevo si ya existía uno");
	}
		
	@Test
	void testCreatePetInvalidAge() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity newPet = factory.manufacturePojo(PetEntity.class);
			newPet.setAge(0); // Regla: Age > 0
			petService.createPet(newPet);
		});
	}
	@Test
	void testCreatePetWithNullStatusDefaultsToAvailable() throws IllegalOperationException {
		// 1. Fabricamos el objeto base con Podam
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		
		// 2. FORZAMOS el null en el status para entrar en la rama del IF
		pet.setStatus(null);
		
		// 3. Llenamos los campos que exige validatePetData para que no lance excepción
		pet.setName("Budy");
		pet.setSpecies("Canino");
		pet.setBreed("Pug");
		pet.setSex("Macho");
		pet.setSize("Pequeño");
		pet.setAge(2); // Edad > 0
		pet.setOrigin("Rescatado de refugio");
		pet.setSpaceRequired("Apartamento");
		pet.setGoodWithKids(true);
		pet.setGoodWithPets(true);

		// 4. Ejecutamos el servicio
		PetEntity result = petService.createPet(pet);

		// 5. VERIFICACIONES (Assersions)
		assertNotNull(result);
		// Aquí es donde matamos el "Partially Covered":
		assertEquals(PetStatus.AVAILABLE, result.getStatus(), 
			"El status debería ser AVAILABLE por defecto si llega nulo");
		
		// Verificamos que persistió en la BD (opcional pero sube coverage de JPA)
		assertNotNull(result.getId());
	}

	@Test
	void testUpdatePetSuccess() throws EntityNotFoundException, IllegalOperationException {
		// 1. Obtenemos una mascota existente de la base de datos (creada en insertData)
		PetEntity existingPet = data.get(0);
		Long id = existingPet.getId();

		// 2. Creamos los datos de actualización con Podam
		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		
		// 3. ASEGURAMOS que los datos sean válidos para pasar validatePetData
		updateData.setName("Rex Updated");
		updateData.setSpecies("Canino");
		updateData.setBreed("Labrador");
		updateData.setSex("Macho");
		updateData.setSize("Grande");
		updateData.setAge(5); 
		updateData.setOrigin("Rescatado");
		updateData.setSpaceRequired("Patio amplio");
		updateData.setGoodWithKids(true);
		updateData.setGoodWithPets(true);
		
		// 4. ASEGURAMOS que el status sea válido para pasar validateStatusChange
		// (Ej: de AVAILABLE a AVAILABLE o a IN_TRIAL)
		updateData.setStatus(PetStatus.AVAILABLE);

		// 5. Ejecutamos el update
		PetEntity result = petService.updatePet(id, updateData);

		// 6. VERIFICACIONES: Esto cubre el setId y el save
		assertNotNull(result);
		assertEquals(id, result.getId(), "El ID debe ser el mismo que el original");
		assertEquals("Rex Updated", result.getName());
		
		// Verificamos en la base de datos real del TestEntityManager
		PetEntity dbPet = entityManager.find(PetEntity.class, id);
		assertEquals("Rex Updated", dbPet.getName());
	}

	@Test
	void testUpdatePetNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			PetEntity updateData = factory.manufacturePojo(PetEntity.class);
			petService.updatePet(999L, updateData); // ID que no existe
		});
	}

	@Test
	void testGetPetsSuccess() {
		// ejecutamos el metodo que queremos cubrir
		List<PetEntity> result = petService.getPets();

		// verificamos contra la data que inserte en el @BeforeEach (insertData)
		assertNotNull(result);
		// Como inserte 3 mascotas en el setUp, la lista debe tener 3
		assertEquals(data.size(), result.size());
		
		// Opcional: Verificar que el primer elemento sea el mismo
		assertEquals(data.get(0).getName(), result.get(0).getName());
	}    

	@Test
	void testGetPetSuccess() throws EntityNotFoundException {
		// se obtiene un elemento de la lista cargada en el setup
		PetEntity pet = data.get(0);
		
		// se consulta a traves del servicio usando el id real
		PetEntity result = petService.getPet(pet.getId());

		// se verifica que el objeto no sea nulo y coincida el nombre
		assertNotNull(result);
		assertEquals(pet.getName(), result.getName());
		assertEquals(pet.getId(), result.getId());
	}

	@Test
	void testGetPetNotFound() {
		// se busca un id que no existe para disparar la excepcion
		assertThrows(EntityNotFoundException.class, () -> {
			petService.getPet(999L);
		});
	}

	@Test
	void testDeletePetSuccess() throws EntityNotFoundException, IllegalOperationException {
		// se selecciona una mascota que no tiene relaciones asociadas
		PetEntity pet = data.get(2);
		Long id = pet.getId();

		// ejecucion del borrado
		petService.deletePet(id);

		// verificacion de que el registro ya no existe en base de datos
		PetEntity deleted = entityManager.find(PetEntity.class, id);
		assertNull(deleted);
	}

	@Test
	void testDeletePetWithAdoptionsFails() {
		// se prepara una mascota con una adopcion simulada
		PetEntity pet = data.get(0);
		
		// se asume que la lista ya esta inicializada por la entidad
		co.edu.udistrital.mdp.pets.entities.AdoptionEntity adoption = new co.edu.udistrital.mdp.pets.entities.AdoptionEntity();
		adoption.setPet(pet);
		pet.getAdoptions().add(adoption);
		
		// se persiste la relacion para la prueba
		entityManager.persist(adoption);
		entityManager.flush();

		// se verifica que el servicio impida el borrado
		assertThrows(IllegalOperationException.class, () -> {
			petService.deletePet(pet.getId());
		});
	}

	@Test
	void testDeletePetWithTrialsFails() {
		// se selecciona una mascota para asignarle un proceso de prueba
		PetEntity pet = data.get(1);
		
		// creacion de un trial dummy para disparar la validacion
		TrialCohabitationEntity trial = new TrialCohabitationEntity();
		trial.setPet(pet);
		pet.getTrials().add(trial);
		
		entityManager.persist(trial);
		entityManager.flush();

		// verificacion de la excepcion por integridad de historial
		assertThrows(IllegalOperationException.class, () -> {
			petService.deletePet(pet.getId());
		});
	}

	@Test
    void testUpdateStatusToAdoptedIllegal() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = data.get(0);
            pet.setStatus(PetStatus.MEDICAL_TREATMENT);
            entityManager.persist(pet);

            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.ADOPTED); // No se puede pasar de Treatment a Adopted
            updateData.setAge(5);
            
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testUpdateWithActiveTrialCollision() {
        PetEntity pet = data.get(0);
        
        // Simular un proceso de prueba activo
        TrialCohabitationEntity activeTrial = factory.manufacturePojo(TrialCohabitationEntity.class);
        activeTrial.setStatus(ProcessStatus.IN_PROGRESS);
        activeTrial.setPet(pet);
        
        pet.getTrials().add(activeTrial);
        
        entityManager.persist(activeTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.IN_TRIAL); // Debería fallar por el trial activo
            updateData.setAge(3);
            petService.updatePet(pet.getId(), updateData);
        });
    }

	@Test
    void testProcessReturnSuccess() throws EntityNotFoundException, IllegalOperationException {
        // Preparamos una mascota que este ADOPTED
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.ADOPTED);
        entityManager.persist(pet);
        entityManager.flush();

        // Ejecutamos el retorno
        PetEntity returnedPet = petService.processReturn(pet.getId());

        // verificamos que el estado volvio a AVAILABLE
        assertEquals(PetStatus.AVAILABLE, returnedPet.getStatus());
        
        // verificamos en bd
        PetEntity dbPet = entityManager.find(PetEntity.class, pet.getId());
        assertEquals(PetStatus.AVAILABLE, dbPet.getStatus());
    }

    @Test
    void testProcessReturnAlreadyAvailable() {
        // Intentar retornar una mascota que ya está disponible debe fallar
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = data.get(0); // Ya está AVAILABLE por el insertData()
            petService.processReturn(pet.getId());
        });
    }

    @Test
    void testDeletePetWithHistory() {
        PetEntity pet = data.get(1);
        
        // Simular historial de pruebas (aunque estén terminadas)
        TrialCohabitationEntity oldTrial = factory.manufacturePojo(TrialCohabitationEntity.class);
        oldTrial.setStatus(ProcessStatus.COMPLETED);
        oldTrial.setPet(pet);
        
        pet.getTrials().add(oldTrial);
        
        entityManager.persist(oldTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            petService.deletePet(pet.getId());
        });
    }

	// --- Additional Tests for validatePetData (via createPet) ---
	@Test
	void testCreatePetWithNullBreed() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setBreed(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptyBreed() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setBreed("   ");
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNullSex() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSex(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptySex() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSex("   ");
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNullSize() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSize(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptySize() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSize("   ");
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNullOrigin() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setOrigin(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptyOrigin() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setOrigin("   ");
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithNullSpaceRequired() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSpaceRequired(null);
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	@Test
	void testCreatePetWithEmptySpaceRequired() {
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		pet.setSpaceRequired("   ");
		assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
	}

	// --- Additional Tests for validateStatusChange (via updatePet) ---
	@Test
	void testUpdatePetStatusToInTrialSuccess() throws EntityNotFoundException, IllegalOperationException {
		PetEntity pet = data.get(0); // AVAILABLE
		
		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.IN_TRIAL); 
		updateData.setAge(pet.getAge()); // Asegurar que age sea valido
		updateData.setName(pet.getName()); // Asegurar que nombre sea valido
		updateData.setSpecies(pet.getSpecies());
		updateData.setBreed(pet.getBreed());
		updateData.setSex(pet.getSex());
		updateData.setSize(pet.getSize());
		updateData.setOrigin(pet.getOrigin());
		updateData.setSpaceRequired(pet.getSpaceRequired());
		updateData.setGoodWithKids(pet.getGoodWithKids());
		updateData.setGoodWithPets(pet.getGoodWithPets());
		
		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.IN_TRIAL, result.getStatus());
	}

	@Test
	void testUpdatePetStatusFromInTrialToAdoptedSuccess() throws EntityNotFoundException, IllegalOperationException {
		// Preparamos una mascota en estado IN_TRIAL
		PetEntity pet = data.get(0); 
		pet.setStatus(PetStatus.IN_TRIAL);
		entityManager.persist(pet);
		entityManager.flush();

		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.ADOPTED); 
		updateData.setAge(pet.getAge()); 
		updateData.setName(pet.getName()); 
		updateData.setSpecies(pet.getSpecies());
		updateData.setBreed(pet.getBreed());
		updateData.setSex(pet.getSex());
		updateData.setSize(pet.getSize());
		updateData.setOrigin(pet.getOrigin());
		updateData.setSpaceRequired(pet.getSpaceRequired());
		updateData.setGoodWithKids(pet.getGoodWithKids());
		updateData.setGoodWithPets(pet.getGoodWithPets());

		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.ADOPTED, result.getStatus());
	}

	// --- Additional Tests for processReturn ---
	@Test
	void testProcessReturnNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			petService.processReturn(999L); // ID que no existe
		});
	}

	// --- Additional Tests for deletePet ---
	@Test
	void testDeletePetNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			petService.deletePet(999L); // ID que no existe
		});
	}

    // --- Additional Tests for line 62 (validateStatusChange) ---
	@Test
	void testUpdatePetStatusToInTrialWhenTrialsAreNullSuccess() throws EntityNotFoundException, IllegalOperationException {
		PetEntity pet = data.get(0); // AVAILABLE
		pet.setTrials(null); // Set trials to null
		entityManager.persist(pet);
		entityManager.flush();

		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.IN_TRIAL); 
		updateData.setAge(pet.getAge()); 
		updateData.setName(pet.getName()); 
		updateData.setSpecies(pet.getSpecies());
		updateData.setBreed(pet.getBreed());
		updateData.setSex(pet.getSex());
		updateData.setSize(pet.getSize());
		updateData.setOrigin(pet.getOrigin());
		updateData.setSpaceRequired(pet.getSpaceRequired());
		updateData.setGoodWithKids(pet.getGoodWithKids());
		updateData.setGoodWithPets(pet.getGoodWithPets());
		
		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.IN_TRIAL, result.getStatus());
	}

	@Test
	void testUpdatePetStatusToInTrialWhenNoActiveTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
		PetEntity pet = data.get(0); // AVAILABLE
		// Ensure trials list is not null, but contains no IN_PROGRESS trials
		pet.setTrials(new ArrayList<>()); 
		entityManager.persist(pet);
		entityManager.flush();

		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.IN_TRIAL); 
		updateData.setAge(pet.getAge()); 
		updateData.setName(pet.getName()); 
		updateData.setSpecies(pet.getSpecies());
		updateData.setBreed(pet.getBreed());
		updateData.setSex(pet.getSex());
		updateData.setSize(pet.getSize());
		updateData.setOrigin(pet.getOrigin());
		updateData.setSpaceRequired(pet.getSpaceRequired());
		updateData.setGoodWithKids(pet.getGoodWithKids());
		updateData.setGoodWithPets(pet.getGoodWithPets());
		
		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.IN_TRIAL, result.getStatus());
	}

    // --- Additional Tests for line 145 (deletePet - adoptions) ---
    @Test
    void testDeletePetWithNullAdoptionsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setAdoptions(null); // Set adoptions to null
        // Ensure no trials either to avoid other exceptions
        pet.setTrials(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testDeletePetWithEmptyAdoptionsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setAdoptions(new ArrayList<>()); // Set adoptions to an empty list
        // Ensure no trials either to avoid other exceptions
        pet.setTrials(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    // --- Additional Tests for line 149 (deletePet - trials) ---
    @Test
    void testDeletePetWithNullTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(null); // Set trials to null
        // Ensure no adoptions either to avoid other exceptions
        pet.setAdoptions(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testDeletePetWithEmptyTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(new ArrayList<>()); // Set trials to an empty list
        // Ensure no adoptions either to avoid other exceptions
        pet.setAdoptions(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

	@Test
	void testUpdatePetStatusToInTrialWhenOnlyCompletedTrialsExistSuccess() throws EntityNotFoundException, IllegalOperationException {
		PetEntity pet = data.get(0); // AVAILABLE
		
		// Create a completed trial
		TrialCohabitationEntity completedTrial = new TrialCohabitationEntity();
		completedTrial.setStatus(ProcessStatus.COMPLETED);
		completedTrial.setPet(pet);
		
		// Initialize the trials list if null, then add the completed trial
		if (pet.getTrials() == null) {
			pet.setTrials(new ArrayList<>());
		}
		pet.getTrials().add(completedTrial);
		
		entityManager.persist(completedTrial); // Persist the trial first
		entityManager.persist(pet); // Then persist the pet with the associated trial
		entityManager.flush();

		PetEntity updateData = factory.manufacturePojo(PetEntity.class);
		updateData.setStatus(PetStatus.IN_TRIAL); 
		updateData.setAge(pet.getAge()); 
		updateData.setName(pet.getName()); 
		updateData.setSpecies(pet.getSpecies());
		updateData.setBreed(pet.getBreed());
		updateData.setSex(pet.getSex());
		updateData.setSize(pet.getSize());
		updateData.setOrigin(pet.getOrigin());
		updateData.setSpaceRequired(pet.getSpaceRequired());
		updateData.setGoodWithKids(pet.getGoodWithKids());
		updateData.setGoodWithPets(pet.getGoodWithPets());
		
		PetEntity result = petService.updatePet(pet.getId(), updateData);
		
		assertEquals(PetStatus.IN_TRIAL, result.getStatus());
	}
}
