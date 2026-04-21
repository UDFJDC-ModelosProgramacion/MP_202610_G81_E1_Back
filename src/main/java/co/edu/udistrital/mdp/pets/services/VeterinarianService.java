package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VeterinarianService extends UserService {

    @Autowired
    private ModelMapper modelMapper;

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
        VeterinarianEntity vet = (VeterinarianEntity) userEntity;
        validateVeterinarianData(vet);
        return super.createUser(vet);
    }

    @Override
    @Transactional
    public UserEntity updateUser(Long userId, UserEntity user) 
            throws EntityNotFoundException, IllegalOperationException {
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

    @Transactional(readOnly = true)
    public List<VeterinarianDTO> findAllVets() {
        return getUsers().stream()
                .filter(VeterinarianEntity.class::isInstance)
                .map(e -> modelMapper.map(e, VeterinarianDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinarianDetailDTO getVetDetail(Long id) throws EntityNotFoundException {
        UserEntity user = getUser(id);
        if (!(user instanceof VeterinarianEntity)) {
            throw new EntityNotFoundException("User with ID " + id + " is not a veterinarian.");
        }
        return modelMapper.map(user, VeterinarianDetailDTO.class);
    }

    @Transactional
    public VeterinarianDTO createFromDTO(VeterinarianDTO dto) throws IllegalOperationException {
        VeterinarianEntity entity = modelMapper.map(dto, VeterinarianEntity.class);
        UserEntity created = this.createUser(entity);
        return modelMapper.map(created, VeterinarianDTO.class);
    }

    @Transactional
    public VeterinarianDTO updateFromDTO(Long id, VeterinarianDTO dto) 
            throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = modelMapper.map(dto, VeterinarianEntity.class);
        UserEntity updated = this.updateUser(id, entity);
        return modelMapper.map(updated, VeterinarianDTO.class);
    }

    @Transactional(readOnly = true)
	public List<NotificationEntity> getNotifications(Long id) throws EntityNotFoundException {
		UserEntity user = getUser(id);
		return user.getNotifications();
	}

	@Transactional(readOnly = true)
	public List<VaccinationRecordEntity> getVaccinationsEntities(Long id) throws EntityNotFoundException {
		VeterinarianEntity vet = (VeterinarianEntity) getUser(id);
		return vet.getVaccinationRecords();
	}

	@Transactional(readOnly = true)
	public List<MedicalEventEntity> getMedicalEventsEntities(Long id) throws EntityNotFoundException {
		VeterinarianEntity vet = (VeterinarianEntity) getUser(id);
		return vet.getMedicalEvents();
	}

	@Transactional(readOnly = true)
	public List<AdoptionFollowUpEntity> getFollowUpsEntities(Long id) throws EntityNotFoundException {
		VeterinarianEntity vet = (VeterinarianEntity) getUser(id);
		return vet.getAdoptionFollowUps();
	}
}

