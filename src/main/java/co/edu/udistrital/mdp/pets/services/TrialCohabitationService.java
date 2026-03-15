package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.TrialCohabitationRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrialCohabitationService {

    @Autowired
    private TrialCohabitationRepository trialCohabitationRepository;

    // Valores permitidos del catalogo para el resultado
    private static final List<String> ALLOWED_RESULTS = Arrays.asList(
        "EN_PROCESO", "EXITOSA", "FALLIDA", "CANCELADA"
    );

    /**
     * Valida los datos obligatorios de la convivencia de prueba.
     * @param trial Entidad de convivencia a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(TrialCohabitationEntity trial) throws IllegalOperationException {
        if (trial == null) {
            throw new IllegalOperationException("Trial cohabitation data cannot be null");
        }
        
        // Regla: La fecha de inicio es obligatoria y no puede estar vacia
        if (trial.getStartDate() == null) {
            throw new IllegalOperationException("Start date is mandatory and cannot be empty");
        }
        
        // Regla: La fecha de fin es obligatoria y no puede estar vacia
        if (trial.getEndDate() == null) {
            throw new IllegalOperationException("End date is mandatory and cannot be empty");
        }
        
        // Validacion adicional: fecha de fin debe ser posterior a fecha de inicio
        if (trial.getEndDate().isBefore(trial.getStartDate())) {
            throw new IllegalOperationException("End date must be after start date");
        }
        
        // Regla: El campo resultado solo acepta valores del catalogo
        if (trial.getResult() != null && !ALLOWED_RESULTS.contains(trial.getResult())) {
            throw new IllegalOperationException(
                "Result must be one of the following values: " + ALLOWED_RESULTS);
        }
    }

    /**
     * Crea una nueva convivencia de prueba en la persistencia.
     * @param trial Entidad de convivencia a crear.
     * @return La convivencia creada.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public TrialCohabitationEntity createTrialCohabitation(TrialCohabitationEntity trial) 
            throws IllegalOperationException {
        log.info("Starting creation process for trial cohabitation");
        
        validateData(trial);
        
        log.info("Trial cohabitation created successfully");
        return trialCohabitationRepository.save(trial);
    }

    /**
     * Obtiene todas las convivencias de prueba.
     * @return Lista de todas las convivencias.
     */
    @Transactional(readOnly = true)
    public List<TrialCohabitationEntity> getTrialCohabitations() {
        log.info("Starting process to consult all trial cohabitations");
        return trialCohabitationRepository.findAll();
    }

    /**
     * Obtiene una convivencia de prueba por su ID.
     * @param trialId ID de la convivencia.
     * @return La convivencia encontrada.
     * @throws EntityNotFoundException Si la convivencia no existe.
     */
    @Transactional(readOnly = true)
    public TrialCohabitationEntity getTrialCohabitation(Long trialId) throws EntityNotFoundException {
        log.info("Starting process to consult trial cohabitation with id = {}", trialId);
        
        return trialCohabitationRepository.findById(trialId)
                .orElseThrow(() -> {
                    log.error("Trial cohabitation with id {} not found", trialId);
                    return new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND);
                });
    }

    /**
     * Actualiza una convivencia de prueba existente.
     * @param trialId ID de la convivencia a actualizar.
     * @param trial Datos actualizados de la convivencia.
     * @return La convivencia actualizada.
     * @throws EntityNotFoundException Si la convivencia no existe.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public TrialCohabitationEntity updateTrialCohabitation(Long trialId, TrialCohabitationEntity trial) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting update process for trial cohabitation with id = {}", trialId);
        
        TrialCohabitationEntity existingTrial = trialCohabitationRepository.findById(trialId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND));
        
        // Regla: El campo resultado solo acepta valores del catalogo
        if (trial.getResult() != null && !ALLOWED_RESULTS.contains(trial.getResult())) {
            throw new IllegalOperationException(
                "Result must be one of the following values: " + ALLOWED_RESULTS);
        }
        
        // Regla: El resultado solo puede cambiar de "EN_PROCESO" a otros estados, no al reves
        String currentResult = existingTrial.getResult();
        String newResult = trial.getResult();
        
        if (currentResult != null && !currentResult.equals("EN_PROCESO") && 
            newResult != null && !newResult.equals(currentResult)) {
            throw new IllegalOperationException(
                "Result can only change from 'EN_PROCESO' to another state. " +
                "Cannot change from '" + currentResult + "' to '" + newResult + "'");
        }
        
        trial.setId(trialId);
        log.info("Trial cohabitation with id = {} updated successfully", trialId);
        return trialCohabitationRepository.save(trial);
    }

    /**
     * Elimina una convivencia de prueba.
     * @param trialId ID de la convivencia a eliminar.
     * @throws EntityNotFoundException Si la convivencia no existe.
     * @throws IllegalOperationException Si la convivencia esta en curso.
     */
    @Transactional
    public void deleteTrialCohabitation(Long trialId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting deletion process for trial cohabitation ID: {}", trialId);
        
        TrialCohabitationEntity trial = trialCohabitationRepository.findById(trialId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND));
        
        // Regla: No se puede eliminar una convivencia de prueba si esta actualmente en curso
        LocalDate today = LocalDate.now();
        if (trial.getStartDate() != null && trial.getEndDate() != null &&
            !today.isBefore(trial.getStartDate()) && !today.isAfter(trial.getEndDate())) {
            log.warn("Attempted to delete trial cohabitation {} but it is in progress", trialId);
            throw new IllegalOperationException("Cannot delete trial cohabitation: It is currently in progress");
        }
        
        trialCohabitationRepository.deleteById(trialId);
        log.info("Trial cohabitation with ID: {} deleted successfully", trialId);
    }
}

