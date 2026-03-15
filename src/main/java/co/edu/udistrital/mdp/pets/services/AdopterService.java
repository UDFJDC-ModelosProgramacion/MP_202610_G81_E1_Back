package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdopterService {

    @Autowired
    private AdopterRepository adopterRepository;

    /**
     * Valida los datos obligatorios del adoptante.
     * @param adopter Entidad del adoptante a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(AdopterEntity adopter) throws IllegalOperationException {
        if (adopter == null) {
            throw new IllegalOperationException("Adopter data cannot be null");
        }
        
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

    /**
     * Crea un nuevo adoptante en la persistencia.
     * @param adopter Entidad del adoptante a crear.
     * @return El adoptante creado.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public AdopterEntity createAdopter(AdopterEntity adopter) throws IllegalOperationException {
        log.info("Starting creation process for adopter");
        
        validateData(adopter);
        
        log.info("Adopter created successfully");
        return adopterRepository.save(adopter);
    }

    /**
     * Obtiene todos los adoptantes.
     * @return Lista de todos los adoptantes.
     */
    @Transactional(readOnly = true)
    public List<AdopterEntity> getAdopters() {
        log.info("Starting process to consult all adopters");
        return adopterRepository.findAll();
    }

    /**
     * Obtiene un adoptante por su ID.
     * @param adopterId ID del adoptante.
     * @return El adoptante encontrado.
     * @throws EntityNotFoundException Si el adoptante no existe.
     */
    @Transactional(readOnly = true)
    public AdopterEntity getAdopter(Long adopterId) throws EntityNotFoundException {
        log.info("Starting process to consult adopter with id = {}", adopterId);
        
        return adopterRepository.findById(adopterId)
                .orElseThrow(() -> {
                    log.error("Adopter with id {} not found", adopterId);
                    return new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
                });
    }

    /**
     * Actualiza un adoptante existente.
     * @param adopterId ID del adoptante a actualizar.
     * @param adopter Datos actualizados del adoptante.
     * @return El adoptante actualizado.
     * @throws EntityNotFoundException Si el adoptante no existe.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public AdopterEntity updateAdopter(Long adopterId, AdopterEntity adopter) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting update process for adopter with id = {}", adopterId);
        
        // Verificar que existe
        adopterRepository.findById(adopterId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND));
        
        // Validar datos (solo housing type es obligatorio en update segun las reglas)
        if (adopter.getHousingType() == null || adopter.getHousingType().isBlank()) {
            throw new IllegalOperationException("Housing type is mandatory and cannot be empty");
        }
        
        adopter.setId(adopterId);
        log.info("Adopter with id = {} updated successfully", adopterId);
        return adopterRepository.save(adopter);
    }

    /**
     * Elimina un adoptante.
     * @param adopterId ID del adoptante a eliminar.
     * @throws EntityNotFoundException Si el adoptante no existe.
     * @throws IllegalOperationException Si el adoptante tiene solicitudes pendientes.
     */
    @Transactional
    public void deleteAdopter(Long adopterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting deletion process for adopter ID: {}", adopterId);
        
        AdopterEntity adopter = adopterRepository.findById(adopterId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND));
        
        // Regla: No se puede eliminar un adoptante si tiene solicitudes de adopcion pendientes o en proceso
        if (adopter.getAdoptionRequests() != null && !adopter.getAdoptionRequests().isEmpty()) {
            log.warn("Attempted to delete adopter {} but it has adoption requests", adopterId);
            throw new IllegalOperationException("Cannot delete adopter: It has pending or in-process adoption requests");
        }
        
        adopterRepository.deleteById(adopterId);
        log.info("Adopter with ID: {} deleted successfully", adopterId);
    }
}
