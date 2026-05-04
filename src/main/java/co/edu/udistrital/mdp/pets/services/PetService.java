package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Logic for conversion and business rules for creation.
     */
    @Transactional
    public PetDTO createFromDTO(PetDTO petDTO) throws IllegalOperationException {
        PetEntity petEntity = modelMapper.map(petDTO, PetEntity.class);
        return modelMapper.map(createPet(petEntity), PetDTO.class);
    }

    /**
     * Logic for conversion and business rules for update.
     */
    @Transactional
    public PetDTO updateFromDTO(Long id, PetDTO petDTO) throws EntityNotFoundException, IllegalOperationException {
        PetEntity petEntity = modelMapper.map(petDTO, PetEntity.class);
        return modelMapper.map(updatePet(id, petEntity), PetDTO.class);
    }

	@Transactional
	public PetDTO processReturnDTO(Long petId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Processing return for pet with id = {}", petId);

		PetEntity pet = petRepository.findById(petId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));

		if (pet.getStatus() == PetStatus.AVAILABLE) {
			throw new IllegalOperationException("Pet is already marked as AVAILABLE.");
		}

		pet.setStatus(PetStatus.AVAILABLE);
		return modelMapper.map(petRepository.save(pet), PetDTO.class);
	}
    /**
     * Internal business rules for pet data.
     */
    private void validatePetData(PetEntity pet) throws IllegalOperationException {
        if (isBlank(pet.getName())) throw new IllegalOperationException("Pet name is mandatory");
        if (isBlank(pet.getSpecies())) throw new IllegalOperationException("Species is mandatory");
        if (isBlank(pet.getBreed())) throw new IllegalOperationException("Breed is mandatory");
        if (isBlank(pet.getSex())) throw new IllegalOperationException("Sex is mandatory");
        if (isBlank(pet.getSize())) throw new IllegalOperationException("Size is mandatory");
        if (pet.getAge() == null || pet.getAge() <= 0) 
            throw new IllegalOperationException("Age must be greater than 0");
        if (isBlank(pet.getOrigin())) throw new IllegalOperationException("Origin/Arrival history is mandatory");
        if (pet.getGoodWithKids() == null) throw new IllegalOperationException("Must define if pet is good with kids");
        if (pet.getGoodWithPets() == null) throw new IllegalOperationException("Must define if pet is good with other pets");
        if (isBlank(pet.getSpaceRequired())) throw new IllegalOperationException("Space requirements must be defined");
    }

    private void validateStatusChange(PetEntity existing, PetStatus nextStatus) throws IllegalOperationException {
        PetStatus current = existing.getStatus();
        if (current == nextStatus) return;
        if (current == PetStatus.ADOPTED) {
            throw new IllegalOperationException("Pet is already adopted and cannot change status.");
        }
        if (nextStatus == PetStatus.IN_TRIAL) {
            boolean hasActiveTrial = existing.getTrials() != null && existing.getTrials().stream()
                .anyMatch(trial -> trial.getStatus() == co.edu.udistrital.mdp.pets.enums.ProcessStatus.IN_PROGRESS);
            if (hasActiveTrial) throw new IllegalOperationException("Pet is already in an active cohabitation trial.");
        }
        if (nextStatus == PetStatus.ADOPTED && (current != PetStatus.AVAILABLE && current != PetStatus.IN_TRIAL)) {
            throw new IllegalOperationException("Pet must be AVAILABLE or IN_TRIAL to be marked as ADOPTED.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Transactional
    public PetEntity createPet(PetEntity pet) throws IllegalOperationException {
        log.info("Creating pet entity: {}", pet.getName());
        if (pet.getStatus() == null) pet.setStatus(PetStatus.AVAILABLE);
        validatePetData(pet);
        
        if (pet.getMedicalHistory() == null) {
            co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity history = new co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity();
            history.setPet(pet);
            pet.setMedicalHistory(history);
        }
        return petRepository.save(pet);
    }

    @Transactional
    public PetEntity updatePet(Long petId, PetEntity pet) throws EntityNotFoundException, IllegalOperationException {
        PetEntity existing = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));

        validatePetData(pet);
        validateStatusChange(existing, pet.getStatus());

        // Update editable fields
        existing.setName(pet.getName());
        existing.setTemperament(pet.getTemperament());
        existing.setStatus(pet.getStatus());
        existing.setPhotos(pet.getPhotos());
        existing.setAge(pet.getAge());
        existing.setSize(pet.getSize());
        existing.setSpecialNeeds(pet.getSpecialNeeds());
        existing.setDescription(pet.getDescription());
        existing.setActivityLevel(pet.getActivityLevel());
        existing.setGoodWithKids(pet.getGoodWithKids());
        existing.setGoodWithPets(pet.getGoodWithPets());
        existing.setSpaceRequired(pet.getSpaceRequired());

        return petRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<PetEntity> getPetsEntities(String species, String size, PetStatus status) {
        log.info("Filtering pets by Species: {}, Size: {}, Status: {}", species, size, status);
        if (species == null && size == null && status == null) {
            return petRepository.findAll();
        }
        return petRepository.findByFilters(species, size, status);
    }

    @Transactional(readOnly = true)
    public PetEntity getPetEntity(Long petId) throws EntityNotFoundException {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));
    }

    @Transactional
    public void deletePet(Long petId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting pet with id = {}", petId);
        PetEntity pet = getPetEntity(petId);
        if (pet.getAdoptions() != null && !pet.getAdoptions().isEmpty()) {
            throw new IllegalOperationException("Cannot delete pet: It has existing adoption records.");
        }
        if (pet.getTrials() != null && !pet.getTrials().isEmpty()) {
            throw new IllegalOperationException("Cannot delete pet: It has cohabitation trials history.");
        }
        petRepository.deleteById(petId);
    }
}
