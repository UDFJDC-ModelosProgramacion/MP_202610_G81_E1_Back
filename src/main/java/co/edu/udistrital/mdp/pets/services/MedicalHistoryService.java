package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository repository;

    /**
     * Valida los datos obligatorios de la historia médica.
     * @param history Entidad a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas.
     */
    private void validateData(MedicalHistoryEntity history) throws IllegalOperationException {
        if (history == null) {
            throw new IllegalOperationException("La historia clínica no puede ser nula.");
        }
        if (history.getPet() == null) {
            throw new IllegalOperationException("La historia clínica debe estar vinculada obligatoriamente a una mascota.");
        }
    }

    /**
     * Crea una nueva historia clínica.
     * @param history Entidad a crear.
     * @return La historia clínica creada.
     * @throws IllegalOperationException Si los datos no cumplen las reglas.
     */
    @Transactional
    public MedicalHistoryEntity createMedicalHistory(MedicalHistoryEntity history) throws IllegalOperationException {
        log.info("Iniciando la creación de la historia clínica");
        validateData(history);
        log.info("Historia clínica creada exitosamente");
        return repository.save(history);
    }

    /**
     * Obtiene todas las historias clínicas.
     * @return Lista de historias clínicas.
     */
    @Transactional(readOnly = true)
    public List<MedicalHistoryEntity> getMedicalHistories() {
        log.info("Consultando todas las historias clínicas");
        return repository.findAll();
    }

    /**
     * Obtiene una historia clínica por su ID.
     * @param historyId ID de la historia.
     * @return La historia encontrada.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional(readOnly = true)
    public MedicalHistoryEntity getMedicalHistory(Long historyId) throws EntityNotFoundException {
        log.info("Consultando historia clínica con id = {}", historyId);
        return repository.findById(historyId)
                .orElseThrow(() -> {
                    log.error("Historia clínica con id {} no encontrada", historyId);
                    return new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND);
                });
    }

    /**
     * Actualiza una historia clínica existente.
     * @param historyId ID a actualizar.
     * @param history Datos actualizados.
     * @return La historia clínica actualizada.
     * @throws EntityNotFoundException Si no existe.
     * @throws IllegalOperationException Si los datos no cumplen las reglas.
     */
    @Transactional
    public MedicalHistoryEntity updateMedicalHistory(Long historyId, MedicalHistoryEntity history) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando actualización de historia clínica con id = {}", historyId);
        
        repository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND));
        
        validateData(history);
        history.setId(historyId);
        
        log.info("Historia clínica con id = {} actualizada exitosamente", historyId);
        return repository.save(history);
    }

    /**
     * Elimina una historia clínica.
     * @param historyId ID a eliminar.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional
    public void deleteMedicalHistory(Long historyId) throws EntityNotFoundException {
        log.info("Iniciando eliminación de la historia clínica ID: {}", historyId);
        
        if (!repository.existsById(historyId)) {
            log.error("Intento de eliminar historia clínica inexistente con ID: {}", historyId);
            throw new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND);
        }
        
        repository.deleteById(historyId);
        log.info("Historia clínica ID: {} eliminada exitosamente", historyId);
    }
}