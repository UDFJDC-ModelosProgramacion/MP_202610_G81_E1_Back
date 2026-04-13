package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdoptionService {

    @Autowired
    private AdoptionRepository adoptionRepository;

    /**
     * Valida los datos obligatorios de la adopcion.
     * @param adoption Entidad de adopcion a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(AdoptionEntity adoption) throws IllegalOperationException {
        if (adoption == null) {
            throw new IllegalOperationException("Adoption data cannot be null");
        }
        
        // Regla: La fecha de adopcion es obligatoria y no puede estar vacia
        if (adoption.getAdoptionDate() == null) {
            throw new IllegalOperationException("Adoption date is mandatory and cannot be empty");
        }
    }

    /**
     * Crea una nueva adopcion en la persistencia.
     * @param adoption Entidad de adopcion a crear.
     * @return La adopcion creada.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public AdoptionEntity createAdoption(AdoptionEntity adoption) throws IllegalOperationException {
        log.info("Starting creation process for adoption");
        
        validateData(adoption);
        
        log.info("Adoption created successfully");
        return adoptionRepository.save(adoption);
    }

    /**
     * Obtiene todas las adopciones.
     * @return Lista de todas las adopciones.
     */
    @Transactional(readOnly = true)
    public List<AdoptionEntity> getAdoptions() {
        log.info("Starting process to consult all adoptions");
        return adoptionRepository.findAll();
    }

    /**
     * Obtiene una adopcion por su ID.
     * @param adoptionId ID de la adopcion.
     * @return La adopcion encontrada.
     * @throws EntityNotFoundException Si la adopcion no existe.
     */
    @Transactional(readOnly = true)
    public AdoptionEntity getAdoption(Long adoptionId) throws EntityNotFoundException {
        log.info("Starting process to consult adoption with id = {}", adoptionId);
        
        return adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> {
                    log.error("Adoption with id {} not found", adoptionId);
                    return new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);
                });
    }

    /**
     * Actualiza una adopcion existente.
     * @param adoptionId ID de la adopcion a actualizar.
     * @param adoption Datos actualizados de la adopcion.
     * @return La adopcion actualizada.
     * @throws EntityNotFoundException Si la adopcion no existe.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public AdoptionEntity updateAdoption(Long adoptionId, AdoptionEntity adoption) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting update process for adoption with id = {}", adoptionId);
        
        AdoptionEntity existingAdoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND));
        
        // Regla: Una vez registrada la adopcion, la fecha de adopcion no puede ser modificada
        if (adoption.getAdoptionDate() != null && 
            !adoption.getAdoptionDate().equals(existingAdoption.getAdoptionDate())) {
            throw new IllegalOperationException("Adoption date cannot be modified once registered");
        }
        
        adoption.setId(adoptionId);
        adoption.setAdoptionDate(existingAdoption.getAdoptionDate()); // Mantener la fecha original
        log.info("Adoption with id = {} updated successfully", adoptionId);
        return adoptionRepository.save(adoption);
    }

    /**
     * Elimina una adopcion.
     * @param adoptionId ID de la adopcion a eliminar.
     * @throws EntityNotFoundException Si la adopcion no existe.
     * @throws IllegalOperationException Si la adopcion tiene seguimientos asociados.
     */
    @Transactional
    public void deleteAdoption(Long adoptionId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting deletion process for adoption ID: {}", adoptionId);
        
        AdoptionEntity adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND));
        
        // Regla: No se puede eliminar una adopcion si tiene seguimientos asociados
        if (adoption.getFollowUps() != null && !adoption.getFollowUps().isEmpty()) {
            log.warn("Attempted to delete adoption {} but it has follow-ups", adoptionId);
            throw new IllegalOperationException("Cannot delete adoption: It has follow-ups associated");
        }
        
        adoptionRepository.deleteById(adoptionId);
        log.info("Adoption with ID: {} deleted successfully", adoptionId);
    }
}

