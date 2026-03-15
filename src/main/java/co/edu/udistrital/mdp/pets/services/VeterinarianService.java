package co.edu.udistrital.mdp.pets.services;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VeterinarianService extends UserService {

    /**
     * Valida datos específicos de la especialidad y disponibilidad del veterinario.
     */
    private void validateVeterinarianData(VeterinarianEntity vet) throws IllegalOperationException {
        if (vet.getSpecialty() == null || vet.getSpecialty().isBlank()) {
            throw new IllegalOperationException("Medical specialty is mandatory (e.g., Surgeon, General Practitioner)");
        }
        if (vet.getAvailability() == null || vet.getAvailability().isBlank()) {
            throw new IllegalOperationException("Work availability and schedule must be defined");
        }
    }

    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity) throws IllegalOperationException {
        log.info("Starting creation process for veterinarian: {}", userEntity.getEmail());
        
        VeterinarianEntity vet = (VeterinarianEntity) userEntity;
        validateVeterinarianData(vet);
        
        return super.createUser(vet);
    }

    @Override
    @Transactional
    public UserEntity updateUser(Long userId, UserEntity user) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating veterinarian with id = {}", userId);
        
        VeterinarianEntity vet = (VeterinarianEntity) user;
        validateVeterinarianData(vet);
        
        return super.updateUser(userId, vet);
    }

    /**
     * Implementacion del metodo abstracto para proteger la integridad medica.
     * No permite borrar al veterinario si tiene registros en el historial medico o de vacunas.
     */
	@Override
	protected void validateDeletion(Long userId) throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity vet = (VeterinarianEntity) userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

        //  seguimientos post-adopcion (AdoptionFollowUp)
        if (vet.getAdoptionFollowUps() != null && !vet.getAdoptionFollowUps().isEmpty()) {
            log.warn("Attempted to delete veterinarian {} with active adoption follow-ups", userId);
            throw new IllegalOperationException("Cannot delete veterinarian: They are assigned to active adoption follow-ups.");
        }

        // registros medicos o de vacunacion
        if ((vet.getMedicalEvents() != null && !vet.getMedicalEvents().isEmpty()) || 
            (vet.getVaccinationRecords() != null && !vet.getVaccinationRecords().isEmpty())) {
            log.warn("Attempted to delete veterinarian {} with medical history records", userId);
            throw new IllegalOperationException("Cannot delete veterinarian: They have recorded medical events or vaccinations.");
        }
    }
}

