package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.dto.AdopterDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdopterService extends UserService {

    @Autowired
    private ModelMapper modelMapper;

	private void validateAdopterData(AdopterEntity adopter) throws IllegalOperationException {
        if (adopter.getHasOtherPets() == null) {
            throw new IllegalOperationException("The field 'hasOtherPets' is mandatory");
        }
        if (adopter.getHasChildren() == null) {
            throw new IllegalOperationException("The field 'hasChildren' is mandatory");
        }
        if (adopter.getHousingType() == null || adopter.getHousingType().isBlank()) {
            throw new IllegalOperationException("Housing type is mandatory");
        }
    }
    /**
     * Crea un adoptante a partir de un DTO. 
     * Centraliza la conversión para limpiar el Controller.
     */
	@Override
	@Transactional
	public UserEntity createUser(UserEntity userEntity) throws IllegalOperationException {
		log.info("Starting creation process for adopter: {}", userEntity.getEmail());
		AdopterEntity adopter = (AdopterEntity) userEntity;
		
		validateAdopterData(adopter); 
		
		return super.createUser(adopter);
	}

    /**
     * Actualiza un adoptante a partir de un DTO.
     */
    @Override
	@Transactional
	public UserEntity updateUser(Long userId, UserEntity user) 
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Updating adopter with id = {}", userId);
		AdopterEntity adopter = (AdopterEntity) user;
		
		// LLAMAR A LA VALIDACIÓN AQUÍ TAMBIÉN
		validateAdopterData(adopter);
		
		return super.updateUser(userId, adopter);
	}

    @Transactional(readOnly = true)
    public List<AdoptionEntity> getAdoptionsByAdopter(Long userId) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        return adopter.getAdoptions();
    }

    @Transactional(readOnly = true)
    public List<AdoptionRequestEntity> getRequestsByAdopter(Long userId) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        return adopter.getAdoptionRequests();
    }

    @Override
    protected void validateDeletion(Long userId) throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity adopter = (AdopterEntity) userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        
        if (adopter.getAdoptionRequests() != null && !adopter.getAdoptionRequests().isEmpty()) {
            throw new IllegalOperationException("Cannot delete adopter with active requests.");
        }
        if (adopter.getAdoptions() != null && !adopter.getAdoptions().isEmpty()) {
            throw new IllegalOperationException("Cannot delete adopter with adoption records.");
        }
    }

	@Transactional
	public AdopterDTO createFromDTO(AdopterDTO dto) throws IllegalOperationException {
		// La conversión ocurre AQUÍ, en el Service
		AdopterEntity entity = modelMapper.map(dto, AdopterEntity.class);
		// Llamamos al createUser que ya tiene el validateAdopterData
		UserEntity created = this.createUser(entity); 
		return modelMapper.map(created, AdopterDTO.class);
	}

	@Transactional
	public AdopterDTO updateFromDTO(Long id, AdopterDTO dto) throws EntityNotFoundException, IllegalOperationException {
		AdopterEntity entity = modelMapper.map(dto, AdopterEntity.class);
		// Llamamos al updateUser que ya tiene el validateAdopterData
		UserEntity updated = this.updateUser(id, entity);
		return modelMapper.map(updated, AdopterDTO.class);
	}
}
