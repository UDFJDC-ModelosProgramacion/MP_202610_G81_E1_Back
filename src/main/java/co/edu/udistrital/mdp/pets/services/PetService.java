package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Valida reglas de negocio: nombre, especie, raza, descripción física, 
     * historia de llegada y compatibilidad.
     */
    private void validatePetData(PetEntity pet) throws IllegalOperationException {
        // campos obligatorios
        if (isBlank(pet.getName())) throw new IllegalOperationException("Pet name is mandatory");
        if (isBlank(pet.getSpecies())) throw new IllegalOperationException("Species is mandatory");
        if (isBlank(pet.getBreed())) throw new IllegalOperationException("Breed is mandatory");

        // descripcion fisica
        if (isBlank(pet.getSex())) throw new IllegalOperationException("Sex is mandatory");
        if (isBlank(pet.getSize())) throw new IllegalOperationException("Size is mandatory");
        if (pet.getAge() == null || pet.getAge() <= 0) 
            throw new IllegalOperationException("Age must be greater than 0");

        // historia de llegada
        if (isBlank(pet.getOrigin())) throw new IllegalOperationException("Origin/Arrival history is mandatory");

        // compatibilidad y espacio
        if (pet.getGoodWithKids() == null) throw new IllegalOperationException("Must define if pet is good with kids");
        if (pet.getGoodWithPets() == null) throw new IllegalOperationException("Must define if pet is good with other pets");
        if (isBlank(pet.getSpaceRequired())) throw new IllegalOperationException("Space requirements must be defined");
    }

    /**
     * Maquina de estados: Valida que el cambio de estatus sea lógico.
     */
	private void validateStatusChange(PetEntity existing, PetStatus nextStatus) throws IllegalOperationException {
        PetStatus current = existing.getStatus();
        if (current == nextStatus) return;

        // Regla: No puede ser adoptada si ya esta en estado ADOPTED
        if (current == PetStatus.ADOPTED) {
            throw new IllegalOperationException("Pet is already adopted and cannot change status unless a return is processed.");
        }

        // Regla: Si pasa a IN_TRIAL, verificar que no haya otra prueba activa 
        if (nextStatus == PetStatus.IN_TRIAL) {
            boolean hasActiveTrial = existing.getTrials() != null && existing.getTrials().stream()
                .anyMatch(trial -> trial.getStatus() == co.edu.udistrital.mdp.pets.enums.ProcessStatus.IN_PROGRESS);
            
            if (hasActiveTrial) {
                throw new IllegalOperationException("Pet is already in an active cohabitation trial.");
            }
        }

        // Regla: Solo puede pasar a ADOPTED si esta disponible o en prueba
        if (nextStatus == PetStatus.ADOPTED && (current != PetStatus.AVAILABLE && current != PetStatus.IN_TRIAL)) {
            throw new IllegalOperationException("Pet must be AVAILABLE or IN_TRIAL to be marked as ADOPTED.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

	@Transactional
    public PetEntity createPet(PetEntity pet) throws IllegalOperationException {
        log.info("Creating pet: {}", pet.getName());
        
        if (pet.getStatus() == null) pet.setStatus(PetStatus.AVAILABLE);
        
        validatePetData(pet);

        // logica de Composicion: creamos el historial medico automaticamente
        if (pet.getMedicalHistory() == null) {
            co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity history = 
                new co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity();
            history.setPet(pet);
            pet.setMedicalHistory(history);
        }

        return petRepository.save(pet);
    }

    @Transactional
    public PetEntity updatePet(Long petId, PetEntity pet) throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating pet with id = {}", petId);
        
        PetEntity existing = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));

        validatePetData(pet);
        validateStatusChange(existing, pet.getStatus());
        pet.setId(petId);
        return petRepository.save(pet);
    }

    @Transactional(readOnly = true)
    public List<PetEntity> getPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PetEntity getPet(Long petId) throws EntityNotFoundException {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));
    }

	@Transactional
    public PetEntity processReturn(Long petId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Processing return for pet with id = {}", petId);

        PetEntity pet = getPet(petId);

        // Rule: If it's already available, there's no need to process a return
        if (pet.getStatus() == PetStatus.AVAILABLE) {
            throw new IllegalOperationException("Pet is already marked as AVAILABLE.");
        }

        // Logic: Reset status so it can be adopted again
        pet.setStatus(PetStatus.AVAILABLE);
        
        log.info("Pet with id = {} is now available for adoption again", petId);
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long petId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting pet with id = {}", petId);
        PetEntity pet = getPet(petId);

        // proteccion de integridad: no borrar si tiene procesos de adopcion o pruebas
        if (pet.getAdoptions() != null && !pet.getAdoptions().isEmpty()) {
            throw new IllegalOperationException("Cannot delete pet: It has existing adoption records.");
        }
        
        if (pet.getTrials() != null && !pet.getTrials().isEmpty()) {
            throw new IllegalOperationException("Cannot delete pet: It has history of cohabitation trials.");
        }

        petRepository.deleteById(petId);
    }
}
