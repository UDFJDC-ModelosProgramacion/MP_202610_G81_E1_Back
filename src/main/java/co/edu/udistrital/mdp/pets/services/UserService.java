package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class UserService {

    @Autowired
    protected UserRepository userRepository;

	// LOS HIJOS IMPLEMENTAN SU LOGICA EN ESTE METODO ABSTRACTO
	protected abstract void validateDeletion(Long userId) throws EntityNotFoundException, IllegalOperationException;
    
	
	/**
     * Valida los datos basicos de un usuario 
     */
    private void validateUser(UserEntity user) throws IllegalOperationException {
        if (user == null) throw new IllegalOperationException("User data cannot be null");

        // campos obligatorios no blancos
        if (isBlank(user.getName())) throw new IllegalOperationException("Name is mandatory");
        if (isBlank(user.getEmail())) throw new IllegalOperationException("Email is mandatory");
        if (isBlank(user.getPhone())) throw new IllegalOperationException("Phone is mandatory");
        if (isBlank(user.getPassword())) throw new IllegalOperationException("Password is mandatory");

        // email valido (Regex simple)
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalOperationException("Email format is invalid");
        }

        // telefono solo números
        if (!user.getPhone().matches("[0-9]+")) {
            throw new IllegalOperationException("Phone must contain only numbers");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Transactional
    public UserEntity createUser(UserEntity userEntity) throws IllegalOperationException {
        if (userEntity == null) throw new IllegalOperationException("User data cannot be null");
        log.info("Starting creation process for user: {}", userEntity.getEmail());

        validateUser(userEntity);

        // ynicidad de Email
        if (userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new IllegalOperationException("Email already exists");
        }

        return userRepository.save(userEntity);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserEntity getUser(Long userId) throws EntityNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

	@Transactional
	public UserEntity updateUser(Long userId, UserEntity user) 
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Updating user with id = {}", userId);
		
		if (user == null) {
			throw new IllegalOperationException("User data cannot be null");
		}

		// solo verificamos existencia. si no esta, el orElseThrow se encarga
		userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

		validateUser(user);

		// validar que el nuevo email no lo tenga OTRO usuario
		Optional<UserEntity> userWithSameEmail = userRepository.findByEmail(user.getEmail());
		if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
			throw new IllegalOperationException("New email already exists in another record");
		}

		user.setId(userId);
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long userId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Deleting user with id = {}", userId);
		
		// solo validamos existencia. el optional lanzara la excepcian si no lo encuentra
		userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

		// Se llama al contrato del hijo (Template Method Pattern)
		// Ej: Adopter no puede eliminarse si tiene procesos de adopcion en curso
		validateDeletion(userId);

		userRepository.deleteById(userId);
	}
}
