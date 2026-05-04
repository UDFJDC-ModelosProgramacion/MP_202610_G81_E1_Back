package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.config.ApplicationConfig;
import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({PetService.class, ApplicationConfig.class})
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
        entityManager.getEntityManager().createQuery("delete from AdoptionFollowUpEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ReviewEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionRequestEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from MedicalHistoryEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PetEntity entity = factory.manufacturePojo(PetEntity.class);
            entity.setStatus(PetStatus.AVAILABLE);
            entity.setAge(i + 1);
            entity.setName("Pet" + i);
            entity.setSpecies("Canino");
            entity.setBreed("Criollo");
            entity.setSex("Macho");
            entity.setSize("Mediano");
            entity.setOrigin("Rescate");
            entity.setSpaceRequired("Casa");
            entity.setGoodWithKids(true);
            entity.setGoodWithPets(true);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    private PetEntity createValidPetEntity() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setName("Firulais");
        pet.setSpecies("Canino");
        pet.setBreed("Criollo");
        pet.setSex("Macho");
        pet.setSize("Mediano");
        pet.setAge(3);
        pet.setOrigin("Rescate en vía pública");
        pet.setSpaceRequired("Apartamento o casa");
        pet.setGoodWithKids(true);
        pet.setGoodWithPets(true);
        pet.setStatus(PetStatus.AVAILABLE);
        return pet;
    }

    private PetDTO createValidPetDTO() {
        PetDTO dto = new PetDTO();
        dto.setName("Firulais");
        dto.setSpecies("Canino");
        dto.setBreed("Criollo");
        dto.setSex("Macho");
        dto.setSize("Mediano");
        dto.setAge(3);
        dto.setOrigin("Rescate en vía pública");
        dto.setSpaceRequired("Apartamento o casa");
        dto.setGoodWithKids(true);
        dto.setGoodWithPets(true);
        dto.setStatus(PetStatus.AVAILABLE.name());
        return dto;
    }

    private void fillUpdateDataWithValidData(PetEntity updateData, PetEntity existing) {
        updateData.setName(existing.getName() != null ? existing.getName() : "Rex");
        updateData.setSpecies(existing.getSpecies() != null ? existing.getSpecies() : "Canino");
        updateData.setBreed(existing.getBreed() != null ? existing.getBreed() : "Criollo");
        updateData.setSex(existing.getSex() != null ? existing.getSex() : "Macho");
        updateData.setSize(existing.getSize() != null ? existing.getSize() : "Mediano");
        updateData.setAge(existing.getAge() != null ? existing.getAge() : 3);
        updateData.setOrigin(existing.getOrigin() != null ? existing.getOrigin() : "Rescate");
        updateData.setSpaceRequired(existing.getSpaceRequired() != null ? existing.getSpaceRequired() : "Casa");
        updateData.setGoodWithKids(existing.getGoodWithKids() != null ? existing.getGoodWithKids() : true);
        updateData.setGoodWithPets(existing.getGoodWithPets() != null ? existing.getGoodWithPets() : true);
    }

    @Test
    void testCreatePetWithNullName() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setName(null);
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithEmptySpecies() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setSpecies("   ");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithNullAge() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setAge(null);
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithNegativeAge() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setAge(-5);
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

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
        PetEntity newPet = createValidPetEntity();
        PetEntity result = petService.createPet(newPet);

        assertNotNull(result);
        assertNotNull(result.getMedicalHistory());
        assertEquals(result, result.getMedicalHistory().getPet());
        assertEquals("Firulais", result.getName());
    }

    @Test
    void testUpdatePetSameStatus() throws EntityNotFoundException, IllegalOperationException {
        PetEntity existingPet = data.get(0);
        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(existingPet.getStatus());
        fillUpdateDataWithValidData(updateData, existingPet);

        PetEntity result = petService.updatePet(existingPet.getId(), updateData);

        assertNotNull(result);
        assertEquals(existingPet.getStatus(), result.getStatus());
    }

    @Test
    void testUpdateStatusFromAdoptedFails() {
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.ADOPTED);
        entityManager.persist(pet);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.AVAILABLE);
            fillUpdateDataWithValidData(updateData, pet);
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testUpdateStatusToTrialWithActiveTrialFails() {
        PetEntity pet = data.get(0);
        TrialCohabitationEntity activeTrial = new TrialCohabitationEntity();
        activeTrial.setStatus(ProcessStatus.IN_PROGRESS);
        activeTrial.setPet(pet);
        pet.getTrials().add(activeTrial);

        entityManager.persist(activeTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.IN_TRIAL);
            fillUpdateDataWithValidData(updateData, pet);
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testUpdateStatusToAdoptedFromInvalidStatusFails() {
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.MEDICAL_TREATMENT);
        entityManager.persist(pet);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            updateData.setStatus(PetStatus.ADOPTED);
            fillUpdateDataWithValidData(updateData, pet);
            petService.updatePet(pet.getId(), updateData);
        });
    }

    @Test
    void testUpdateStatusToAdoptedSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(1);
        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.ADOPTED);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.ADOPTED, result.getStatus());
    }

    @Test
    void testCreatePetWithExistingMedicalHistory() throws IllegalOperationException {
        PetEntity pet = createValidPetEntity();
        MedicalHistoryEntity existingHistory = new MedicalHistoryEntity();
        existingHistory.setDescription("Historial previo de vacunas");
        pet.setMedicalHistory(existingHistory);
        existingHistory.setPet(pet);

        PetEntity result = petService.createPet(pet);

        assertSame(existingHistory, result.getMedicalHistory());
    }

    @Test
    void testCreatePetInvalidAge() {
        PetEntity newPet = factory.manufacturePojo(PetEntity.class);
        newPet.setAge(0);
        assertThrows(IllegalOperationException.class, () -> petService.createPet(newPet));
    }

    @Test
    void testCreatePetWithNullStatusDefaultsToAvailable() throws IllegalOperationException {
        PetEntity pet = createValidPetEntity();
        pet.setStatus(null);

        PetEntity result = petService.createPet(pet);

        assertNotNull(result);
        assertEquals(PetStatus.AVAILABLE, result.getStatus());
        assertNotNull(result.getId());
    }

    @Test
    void testUpdatePetSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity existingPet = data.get(0);
        Long id = existingPet.getId();

        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
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
        updateData.setStatus(PetStatus.AVAILABLE);

        PetEntity result = petService.updatePet(id, updateData);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Rex Updated", result.getName());
        PetEntity dbPet = entityManager.find(PetEntity.class, id);
        assertEquals("Rex Updated", dbPet.getName());
    }

    @Test
    void testUpdatePetNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity updateData = factory.manufacturePojo(PetEntity.class);
            petService.updatePet(999L, updateData);
        });
    }

    @Test
    void testGetPetsEntitiesSuccess() {
        List<PetEntity> result = petService.getPetsEntities(null, null, null);

        assertNotNull(result);
        assertEquals(data.size(), result.size());
        assertEquals(data.get(0).getName(), result.get(0).getName());
    }

    @Test
    void testGetPetsEntitiesWithFiltersSuccess() {
        String speciesFilter = data.get(0).getSpecies();
        List<PetEntity> result = petService.getPetsEntities(speciesFilter, null, null);

        assertNotNull(result);
        assertTrue(result.stream().allMatch(p -> p.getSpecies().equals(speciesFilter)));
    }

    @Test
    void testGetPetEntitySuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        PetEntity result = petService.getPetEntity(pet.getId());

        assertNotNull(result);
        assertEquals(pet.getName(), result.getName());
        assertEquals(pet.getId(), result.getId());
    }

    @Test
    void testGetPetEntityNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            petService.getPetEntity(999L);
        });
    }

    @Test
    void testDeletePetSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(2);
        Long id = pet.getId();

        petService.deletePet(id);

        PetEntity deleted = entityManager.find(PetEntity.class, id);
        assertNull(deleted);
    }

    @Test
    void testDeletePetWithAdoptionsFails() {
        PetEntity pet = data.get(0);
        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setPet(pet);
        pet.getAdoptions().add(adoption);

        entityManager.persist(adoption);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> petService.deletePet(pet.getId()));
    }

    @Test
    void testDeletePetWithTrialsFails() {
        PetEntity pet = data.get(1);
        TrialCohabitationEntity trial = new TrialCohabitationEntity();
        trial.setPet(pet);
        pet.getTrials().add(trial);

        entityManager.persist(trial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> petService.deletePet(pet.getId()));
    }

    @Test
    void testProcessReturnDTOSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.ADOPTED);
        entityManager.persist(pet);
        entityManager.flush();

        PetDTO result = petService.processReturnDTO(pet.getId());

        assertEquals(PetStatus.AVAILABLE.name(), result.getStatus());
        PetEntity dbPet = entityManager.find(PetEntity.class, pet.getId());
        assertEquals(PetStatus.AVAILABLE, dbPet.getStatus());
    }

    @Test
    void testProcessReturnDTOAlreadyAvailable() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = data.get(0);
            petService.processReturnDTO(pet.getId());
        });
    }

    @Test
    void testProcessReturnDTONotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            petService.processReturnDTO(999L);
        });
    }

    @Test
    void testDeletePetWithHistory() {
        PetEntity pet = data.get(1);
        TrialCohabitationEntity oldTrial = factory.manufacturePojo(TrialCohabitationEntity.class);
        oldTrial.setStatus(ProcessStatus.COMPLETED);
        oldTrial.setPet(pet);
        pet.getTrials().add(oldTrial);

        entityManager.persist(oldTrial);
        entityManager.flush();

        assertThrows(IllegalOperationException.class, () -> petService.deletePet(pet.getId()));
    }

    @ParameterizedTest
    @MethodSource("nullFields")
    void testCreatePetWithNullMandatoryFields(String fieldName, String nullValue) {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        switch (fieldName) {
            case "breed" -> pet.setBreed(nullValue);
            case "sex" -> pet.setSex(nullValue);
            case "size" -> pet.setSize(nullValue);
            case "origin" -> pet.setOrigin(nullValue);
            case "spaceRequired" -> pet.setSpaceRequired(nullValue);
        }
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> nullFields() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.arguments("breed", null),
            org.junit.jupiter.params.provider.Arguments.arguments("sex", null),
            org.junit.jupiter.params.provider.Arguments.arguments("size", null),
            org.junit.jupiter.params.provider.Arguments.arguments("origin", null),
            org.junit.jupiter.params.provider.Arguments.arguments("spaceRequired", null)
        );
    }

    @Test
    void testCreatePetWithBlankName() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setName("  ");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankSpecies() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setSpecies("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankBreed() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setBreed("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankSex() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setSex("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankSize() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setSize("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankOrigin() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setOrigin("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testCreatePetWithBlankSpaceRequired() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setSpaceRequired("");
        assertThrows(IllegalOperationException.class, () -> petService.createPet(pet));
    }

    @Test
    void testUpdatePetStatusToInTrialSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.IN_TRIAL);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.IN_TRIAL, result.getStatus());
    }

    @Test
    void testUpdatePetStatusFromInTrialToAdoptedSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.IN_TRIAL);
        entityManager.persist(pet);
        entityManager.flush();

        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.ADOPTED);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.ADOPTED, result.getStatus());
    }

    @Test
    void testUpdatePetStatusToInTrialWhenTrialsAreNullSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(null);
        entityManager.persist(pet);
        entityManager.flush();

        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.IN_TRIAL);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.IN_TRIAL, result.getStatus());
    }

    @Test
    void testUpdatePetStatusToInTrialWhenNoActiveTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(new ArrayList<>());
        entityManager.persist(pet);
        entityManager.flush();

        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.IN_TRIAL);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.IN_TRIAL, result.getStatus());
    }

    @Test
    void testDeletePetNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            petService.deletePet(999L);
        });
    }

    @Test
    void testDeletePetWithNullAdoptionsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setAdoptions(null);
        pet.setTrials(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testDeletePetWithEmptyAdoptionsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setAdoptions(new ArrayList<>());
        pet.setTrials(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testDeletePetWithNullTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(null);
        pet.setAdoptions(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testDeletePetWithEmptyTrialsSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        pet.setTrials(new ArrayList<>());
        pet.setAdoptions(null);
        entityManager.persist(pet);
        entityManager.flush();

        petService.deletePet(pet.getId());
        assertNull(entityManager.find(PetEntity.class, pet.getId()));
    }

    @Test
    void testUpdatePetStatusToInTrialWhenOnlyCompletedTrialsExistSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = data.get(0);
        TrialCohabitationEntity completedTrial = new TrialCohabitationEntity();
        completedTrial.setStatus(ProcessStatus.COMPLETED);
        completedTrial.setPet(pet);
        if (pet.getTrials() == null) {
            pet.setTrials(new ArrayList<>());
        }
        pet.getTrials().add(completedTrial);

        entityManager.persist(completedTrial);
        entityManager.persist(pet);
        entityManager.flush();

        PetEntity updateData = factory.manufacturePojo(PetEntity.class);
        updateData.setStatus(PetStatus.IN_TRIAL);
        fillUpdateDataWithValidData(updateData, pet);

        PetEntity result = petService.updatePet(pet.getId(), updateData);

        assertEquals(PetStatus.IN_TRIAL, result.getStatus());
    }

    @Test
    void testCreateFromDTOSuccess() throws IllegalOperationException {
        PetDTO inputDto = createValidPetDTO();

        PetDTO result = petService.createFromDTO(inputDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Firulais", result.getName());
        assertEquals(PetStatus.AVAILABLE.name(), result.getStatus());
    }

    @Test
    void testCreateFromDTOWithNullName() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setName(null);
        assertThrows(IllegalOperationException.class, () -> petService.createFromDTO(petDTO));
    }

    @Test
    void testCreateFromDTOWithNullStatusDefaultsToAvailable() throws IllegalOperationException {
        PetDTO inputDto = createValidPetDTO();
        inputDto.setStatus(null);

        PetDTO result = petService.createFromDTO(inputDto);

        assertNotNull(result);
        assertEquals(PetStatus.AVAILABLE.name(), result.getStatus());
    }

    @Test
    void testUpdateFromDTOSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity existingPet = data.get(0);
        Long id = existingPet.getId();

        PetDTO updateDTO = new PetDTO();
        updateDTO.setName("Rex Updated DTO");
        updateDTO.setSpecies(existingPet.getSpecies());
        updateDTO.setBreed(existingPet.getBreed());
        updateDTO.setSex(existingPet.getSex());
        updateDTO.setSize(existingPet.getSize());
        updateDTO.setAge(existingPet.getAge());
        updateDTO.setOrigin(existingPet.getOrigin());
        updateDTO.setSpaceRequired(existingPet.getSpaceRequired());
        updateDTO.setGoodWithKids(existingPet.getGoodWithKids());
        updateDTO.setGoodWithPets(existingPet.getGoodWithPets());
        updateDTO.setStatus(PetStatus.AVAILABLE.name());

        PetDTO result = petService.updateFromDTO(id, updateDTO);

        assertNotNull(result);
        assertEquals("Rex Updated DTO", result.getName());
    }

    @Test
    void testUpdateFromDTOInvalidStatusChange() {
        PetEntity pet = data.get(0);
        pet.setStatus(PetStatus.ADOPTED);
        entityManager.persist(pet);
        entityManager.flush();

        PetDTO updateDTO = new PetDTO();
        updateDTO.setName(pet.getName());
        updateDTO.setSpecies(pet.getSpecies());
        updateDTO.setBreed(pet.getBreed());
        updateDTO.setSex(pet.getSex());
        updateDTO.setSize(pet.getSize());
        updateDTO.setAge(pet.getAge());
        updateDTO.setOrigin(pet.getOrigin());
        updateDTO.setSpaceRequired(pet.getSpaceRequired());
        updateDTO.setGoodWithKids(pet.getGoodWithKids());
        updateDTO.setGoodWithPets(pet.getGoodWithPets());
        updateDTO.setStatus(PetStatus.AVAILABLE.name());

        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), updateDTO));
    }

    @Test
    void testUpdateFromDTOWithNullName() {
        PetDTO petDTO = new PetDTO();
        petDTO.setName(null);
        petDTO.setSpecies("Canino");
        petDTO.setBreed("Criollo");
        petDTO.setSex("Macho");
        petDTO.setSize("Mediano");
        petDTO.setAge(3);
        petDTO.setOrigin("Rescate");
        petDTO.setSpaceRequired("Casa");
        petDTO.setGoodWithKids(true);
        petDTO.setGoodWithPets(true);
        petDTO.setStatus(PetStatus.AVAILABLE.name());

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNullAge() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setAge(null);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithZeroAge() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setAge(0);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNegativeAge() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setAge(-1);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNullGoodWithKids() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setGoodWithKids(null);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNullGoodWithPets() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setGoodWithPets(null);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptySpecies() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setSpecies("   ");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptyBreed() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setBreed("");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptySex() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setSex("  ");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptySize() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setSize("   ");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNullOrigin() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setOrigin(null);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptyOrigin() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setOrigin(" ");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithNullSpaceRequired() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setSpaceRequired(null);

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTOWithEmptySpaceRequired() {
        PetDTO petDTO = createValidPetDTO();
        petDTO.setSpaceRequired("");

        PetEntity pet = data.get(0);
        assertThrows(IllegalOperationException.class, () -> petService.updateFromDTO(pet.getId(), petDTO));
    }

    @Test
    void testUpdateFromDTONotFound() {
        PetDTO petDTO = createValidPetDTO();
        assertThrows(EntityNotFoundException.class, () -> petService.updateFromDTO(999L, petDTO));
    }

    @Test
    void testFindAllDTOsSuccess() {
        List<PetDTO> result = petService.findAllDTOs(null, null, null);

        assertNotNull(result);
        assertEquals(data.size(), result.size());
    }

    @Test
    void testFindAllDTOsWithFilters() {
        String speciesFilter = data.get(0).getSpecies();
        List<PetDTO> result = petService.findAllDTOs(speciesFilter, null, null);

        assertNotNull(result);
        assertTrue(result.stream().allMatch(p -> p.getSpecies().equals(speciesFilter)));
    }

    @Test
    void testFindDetailDTOSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        PetDetailDTO result = petService.findDetailDTO(pet.getId());

        assertNotNull(result);
        assertEquals(pet.getName(), result.getName());
        assertEquals(pet.getId(), result.getId());
    }

    @Test
    void testFindDetailDTONotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findDetailDTO(999L));
    }

    @Test
    void testFindAdoptionsByPetIdSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setPet(pet);
        pet.getAdoptions().add(adoption);
        entityManager.persist(adoption);
        entityManager.flush();

        List<AdoptionDTO> result = petService.findAdoptionsByPetId(pet.getId());

        assertNotNull(result);
    }

    @Test
    void testFindAdoptionsByPetIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findAdoptionsByPetId(999L));
    }

    @Test
    void testFindRequestsByPetIdSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        request.setPet(pet);
        pet.getAdoptionRequests().add(request);
        entityManager.persist(request);
        entityManager.flush();

        List<AdoptionRequestDTO> result = petService.findRequestsByPetId(pet.getId());

        assertNotNull(result);
    }

    @Test
    void testFindRequestsByPetIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findRequestsByPetId(999L));
    }

    @Test
    void testFindFollowUpsByPetIdSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        AdoptionFollowUpEntity followUp = new AdoptionFollowUpEntity();
        followUp.setPet(pet);
        pet.getFollowUps().add(followUp);
        entityManager.persist(followUp);
        entityManager.flush();

        List<AdoptionFollowUpDTO> result = petService.findFollowUpsByPetId(pet.getId());

        assertNotNull(result);
    }

    @Test
    void testFindFollowUpsByPetIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findFollowUpsByPetId(999L));
    }

    @Test
    void testFindReviewsByPetIdSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        ReviewEntity review = new ReviewEntity();
        review.setPet(pet);
        pet.getReviews().add(review);
        entityManager.persist(review);
        entityManager.flush();

        List<ReviewDTO> result = petService.findReviewsByPetId(pet.getId());

        assertNotNull(result);
    }

    @Test
    void testFindReviewsByPetIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findReviewsByPetId(999L));
    }

    @Test
    void testFindTrialsByPetIdSuccess() throws EntityNotFoundException {
        PetEntity pet = data.get(0);
        TrialCohabitationEntity trial = new TrialCohabitationEntity();
        trial.setPet(pet);
        pet.getTrials().add(trial);
        entityManager.persist(trial);
        entityManager.flush();

        List<TrialCohabitationDTO> result = petService.findTrialsByPetId(pet.getId());

        assertNotNull(result);
    }

    @Test
    void testFindTrialsByPetIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> petService.findTrialsByPetId(999L));
    }
}
