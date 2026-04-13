package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
        pet = factory.manufacturePojo(PetEntity.class);
        pet.setStatus(PetStatus.AVAILABLE);
        pet = entityManager.persist(pet);

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
        pet.setStatus(PetStatus.ADOPTED); 
        entityManager.merge(pet);
        entityManager.flush();

        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);

        assertThrows(IllegalOperationException.class, () -> service.createRequest(request));
    }

    @Test
    void testCreateDuplicateRequestFails() throws IllegalOperationException {
        AdoptionRequestEntity request1 = new AdoptionRequestEntity();
        request1.setPet(pet);
        request1.setAdopter(adopter);
        service.createRequest(request1);

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
        
        service.updateRequestStatus(saved.getId(), "APPROVED");

        assertThrows(IllegalOperationException.class, () -> 
            service.updateRequestStatus(saved.getId(), "REJECTED"));
    }

    @Test
    void testGetRequestNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.getRequest(999L));
    }

	@Test
    void testGetStrategies() {
        ManualApprovalStrategyEntity strategy1 = new ManualApprovalStrategyEntity();
        
        MedicalClearanceStrategyEntity strategy2 = new MedicalClearanceStrategyEntity();
        
        entityManager.persist(strategy1);
        entityManager.persist(strategy2);
        entityManager.flush();

        List<ApprovalStrategyEntity> strategies = service.getStrategies();

        assertNotNull(strategies);
        assertTrue(strategies.size() >= 2);
    }

	@Test
    void testCreateStrategyManual() {
        ApprovalStrategyEntity strategy = service.createStrategy("MANUAL");
        assertNotNull(strategy);
        assertTrue(strategy instanceof ManualApprovalStrategyEntity);
    }

    @Test
    void testCreateStrategyMedical() {
        ApprovalStrategyEntity strategy = service.createStrategy("MEDICAL");
        assertNotNull(strategy);
        assertTrue(strategy instanceof MedicalClearanceStrategyEntity);
    }

    @Test
    void testCreateStrategyScore() {
        ApprovalStrategyEntity strategy = service.createStrategy("SCORE");
        assertNotNull(strategy);
        assertTrue(strategy instanceof ScoreBasedApprovalStrategyEntity);
    }

    @Test
    void testCreateStrategyInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createStrategy("UNKNOWN");
        });
    }

	@Test
    void testCreateRequestWithAutoApprovalStrategy() throws IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);

        ManualApprovalStrategyEntity mockStrategy = org.mockito.Mockito.mock(ManualApprovalStrategyEntity.class);
        org.mockito.Mockito.when(mockStrategy.evaluate(org.mockito.ArgumentMatchers.any())).thenReturn(true);
        
        request.setApprovalStrategy(mockStrategy);

        AdoptionRequestEntity saved = service.createRequest(request);

        assertNotNull(saved);
        assertEquals("APPROVED", saved.getStatus());
        org.mockito.Mockito.verify(mockStrategy).evaluate(request);
    }

	@Test
    void testEvaluateRequestApproved() throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);
        request.setStatus("PENDING");
        request = entityManager.persist(request);

        ManualApprovalStrategyEntity strategy = new ManualApprovalStrategyEntity();
        strategy = entityManager.persist(strategy);
        entityManager.flush();

        AdoptionRequestEntity result = service.evaluateRequest(request.getId(), strategy.getId());

        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        assertEquals(PetStatus.RESERVED, result.getPet().getStatus());
        assertNotNull(result.getApprovalStrategy());
    }

    @Test
    void testEvaluateRequestNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            service.evaluateRequest(999L, 999L);
        });
    }

	@Test
    void testCreateRequestWithNullPetFails() {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(null); 
        request.setAdopter(adopter);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class, () -> 
            service.createRequest(request));
        assertTrue(ex.getMessage().contains("A pet must be associated"));
    }

    @Test
    void testCreateRequestWithNullPetIdFails() {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(new PetEntity()); 
        request.setAdopter(adopter);

        assertThrows(IllegalOperationException.class, () -> 
            service.createRequest(request));
    }

    @Test
    void testCreateRequestWithNullAdopterSuccess() throws IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(null); 

        AdoptionRequestEntity saved = service.createRequest(request);

        assertNotNull(saved);
        assertNull(saved.getAdopter());
        assertEquals("PENDING", saved.getStatus());
    }

    @Test
    void testCreateRequestWithNonExistentPetFails() {
        PetEntity fakePet = new PetEntity();
        fakePet.setId(999L);
        
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(fakePet);
        request.setAdopter(adopter);

        assertThrows(IllegalOperationException.class, () -> 
            service.createRequest(request));
    }
	
	@Test
    void testValidateStatusUpdateFromFinalizedFails() {
        // 1. Crear y persistir una solicitud directamente en estado APPROVED
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);
        request.setStatus("APPROVED");
        
        // Usamos el entityManager para saltarnos la validación de creación y tener el estado deseado
        request = entityManager.persist(request);
        entityManager.flush();

        final Long id = request.getId();

        // 2. Intentar actualizarla debe lanzar IllegalOperationException
        assertThrows(IllegalOperationException.class, () -> 
            service.updateRequestStatus(id, "REJECTED"),
            "Should fail when request is already APPROVED");
            
        // 3. Caso para REJECTED (para asegurar cobertura completa del OR)
        request.setStatus("REJECTED");
        entityManager.merge(request);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> 
            service.updateRequestStatus(id, "APPROVED"),
            "Should fail when request is already REJECTED");
    }

    @Test
    void testValidateStatusUpdateToInvalidStatusFails() throws IllegalOperationException {
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        request.setAdopter(adopter);
        request = service.createRequest(request);

        final Long id = request.getId();
        assertThrows(IllegalOperationException.class, () -> 
            service.updateRequestStatus(id, "WAITING"));
    }
}
