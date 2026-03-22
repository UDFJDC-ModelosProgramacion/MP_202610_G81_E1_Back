package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.VaccinationRecordRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class VaccinationRecordService {

    @Autowired
    private VaccinationRecordRepository repository;

    /**
     * Valida los datos obligatorios del registro de vacunación.
     * @param record Entidad de vacunación a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(VaccinationRecordEntity record) throws IllegalOperationException {
        if (record == null) {
            throw new IllegalOperationException("El registro de vacunación no puede ser nulo.");
        }
        if (record.getApplicationDate() == null || record.getNextDueDate() == null) {
            throw new IllegalOperationException("Las fechas de aplicación y vencimiento son obligatorias.");
        }
        if (record.getNextDueDate().isBefore(record.getApplicationDate())) {
            throw new IllegalOperationException("La fecha de vencimiento no puede ser anterior a la aplicación.");
        }
    }

    /**
     * Crea un nuevo registro de vacunación en la persistencia.
     * @param record Entidad a crear.
     * @return El registro creado.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public VaccinationRecordEntity createVaccinationRecord(VaccinationRecordEntity record) throws IllegalOperationException {
        log.info("Iniciando el proceso de creación del registro de vacunación");
        validateData(record);
        log.info("Registro de vacunación creado exitosamente");
        return repository.save(record);
    }

    /**
     * Obtiene todos los registros de vacunación.
     * @return Lista de todos los registros.
     */
    @Transactional(readOnly = true)
    public List<VaccinationRecordEntity> getVaccinationRecords() {
        log.info("Iniciando proceso para consultar todos los registros de vacunación");
        return repository.findAll();
    }

    /**
     * Obtiene un registro de vacunación por su ID.
     * @param recordId ID del registro.
     * @return El registro encontrado.
     * @throws EntityNotFoundException Si el registro no existe.
     */
    @Transactional(readOnly = true)
    public VaccinationRecordEntity getVaccinationRecord(Long recordId) throws EntityNotFoundException {
        log.info("Consultando registro de vacunación con id = {}", recordId);
        return repository.findById(recordId)
                .orElseThrow(() -> {
                    log.error("Registro de vacunación con id {} no encontrado", recordId);
                    return new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
                });
    }

    /**
     * Actualiza un registro de vacunación existente.
     * @param recordId ID del registro a actualizar.
     * @param record Datos actualizados.
     * @return El registro actualizado.
     * @throws EntityNotFoundException Si el registro no existe.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    @Transactional
    public VaccinationRecordEntity updateVaccinationRecord(Long recordId, VaccinationRecordEntity record) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando actualización para el registro de vacunación con id = {}", recordId);
        
        if (!repository.existsById(recordId)) {
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
        }
        
        validateData(record);
        record.setId(recordId);
        
        log.info("Registro de vacunación con id = {} actualizado exitosamente", recordId);
        return repository.save(record);
    }

    /**
     * Elimina un registro de vacunación.
     * @param recordId ID del registro a eliminar.
     * @throws EntityNotFoundException Si el registro no existe.
     */
    @Transactional
    public void deleteVaccinationRecord(Long recordId) throws EntityNotFoundException {
        log.info("Iniciando proceso de eliminación para el registro de vacunación ID: {}", recordId);
        
        if (!repository.existsById(recordId)) {
            log.error("Intento de eliminar un registro inexistente con ID: {}", recordId);
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
        }
        
        repository.deleteById(recordId);
        log.info("Registro de vacunación con ID: {} eliminado exitosamente", recordId);
    }
}