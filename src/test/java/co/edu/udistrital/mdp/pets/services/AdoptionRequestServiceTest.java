package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.udistrital.mdp.pets.entities.*;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Import(AdoptionRequestService.class)
class AdoptionRequestServiceTest {

    @Autowired
    private AdoptionRequestService service;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();
    private PetEntity pet;
    private AdopterEntity adopter;

    @BeforeEach
    void setUp() {
        // Crear mascota disponible
        pet = factory.manufacturePojo(PetEntity.class);
        pet.setStatus(PetStatus.AVAILABLE);
        pet = entityManager.persist(pet);

        // Crear adoptante (usamos AdopterEntity porque UserEntity es abstracta)
        adopter = factory.manufacturePojo(AdopterEntity.class);
        adopter = entityManager.persist(adopter);
        
        entityManager.flush();
    }

    @Test
    void testCreateRequestSuccess() throws IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);

        AdoptionRequestEntity saved = service.createRequest(request);

        assertNotNull(saved.getId());
        assertEquals("PENDING", saved.getStatus());
        assertNotNull(saved.getRequestDate());
    }

    @Test
    void testCreateRequestPetNotAvailableFails() {
        pet.setStatus(PetStatus.ADOPTED); // Cambiamos a no disponible
        entityManager.merge(pet);
        entityManager.flush();

        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);

        assertThrows(IllegalOperationException.class, () -> service.createRequest(request));
    }

    @Test
    void testCreateDuplicateRequestFails() throws IllegalOperationException {
        // Primera solicitud
        AdoptionRequestEntity request1 = new AdoptionRequestEntity();
        request1.setPet(pet);
        request1.setAdopter(adopter);
        service.createRequest(request1);

        // Segunda solicitud idéntica (debe fallar por regla de negocio)
        AdoptionRequestEntity request2 = new AdoptionRequestEntity();
        request2.setPet(pet);
        request2.setAdopter(adopter);

        assertThrows(IllegalOperationException.class, () -> service.createRequest(request2));
    }

    @Test
    void testUpdateRequestStatusSuccess() throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);
        AdoptionRequestEntity saved = service.createRequest(request);

        AdoptionRequestEntity updated = service.updateRequestStatus(saved.getId(), "APPROVED");
        
        assertEquals("APPROVED", updated.getStatus());
    }

    @Test
    void testUpdateAlreadyFinishedRequestFails() throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);
        AdoptionRequestEntity saved = service.createRequest(request);
        
        // La aprobamos una vez
        service.updateRequestStatus(saved.getId(), "APPROVED");

        // Intentar cambiarla de nuevo debe fallar
        assertThrows(IllegalOperationException.class, () -> 
            service.updateRequestStatus(saved.getId(), "REJECTED"));
    }

    @Test
    void testGetRequestNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.getRequest(999L));
    }
}
