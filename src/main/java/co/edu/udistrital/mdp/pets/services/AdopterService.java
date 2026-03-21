package co.edu.udistrital.mdp.pets.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdopterService extends UserService {

    /**
     * Valida datos especificos del adoptante.
     * @param adopter Entidad del adoptante a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateAdopterData(AdopterEntity adopter) throws IllegalOperationException {
        // Regla: El campo hasOtherPets debe ser un valor booleano, no puede quedar indefinido
        if (adopter.getHasOtherPets() == null) {
            throw new IllegalOperationException("The field 'hasOtherPets' is mandatory and cannot be undefined");
        }
        
        // Regla: El campo hasChildren debe ser un valor booleano, no puede quedar indefinido
        if (adopter.getHasChildren() == null) {
            throw new IllegalOperationException("The field 'hasChildren' is mandatory and cannot be undefined");
        }
        
        // Regla: El campo tipo de vivienda no puede estar vacio o nulo
        if (adopter.getHousingType() == null || adopter.getHousingType().isBlank()) {
            throw new IllegalOperationException("Housing type is mandatory and cannot be empty");
        }
    }

    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity) throws IllegalOperationException {
        log.info("Starting creation process for adopter: {}", userEntity.getEmail());
        
        AdopterEntity adopter = (AdopterEntity) userEntity;
        validateAdopterData(adopter);
        
        return super.createUser(adopter);
    }

    @Override
    @Transactional
    public UserEntity updateUser(Long userId, UserEntity user) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating adopter with id = {}", userId);
        
        AdopterEntity adopter = (AdopterEntity) user;
        validateAdopterData(adopter);
        
        return super.updateUser(userId, adopter);
    }

    /**
     * Implementacion del metodo abstracto para proteger la integridad de adopciones.
     * No permite borrar al adoptante si tiene solicitudes de adopcion pendientes o en proceso.
     */
    @Override
    protected void validateDeletion(Long userId) throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity adopter = (AdopterEntity) userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

        // Regla: No se puede eliminar un adoptante si tiene solicitudes de adopcion pendientes o en proceso
        if (adopter.getAdoptionRequests() != null && !adopter.getAdoptionRequests().isEmpty()) {
            log.warn("Attempted to delete adopter {} with active adoption requests", userId);
            throw new IllegalOperationException("Cannot delete adopter: They have pending or in-process adoption requests.");
        }

        // Regla adicional: No se puede eliminar si tiene adopciones registradas
        if (adopter.getAdoptions() != null && !adopter.getAdoptions().isEmpty()) {
            log.warn("Attempted to delete adopter {} with adoption records", userId);
            throw new IllegalOperationException("Cannot delete adopter: They have adoption records.");
        }
    }
}
