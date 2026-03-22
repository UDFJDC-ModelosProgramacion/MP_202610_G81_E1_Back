package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.VaccineRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class VaccineService {

    @Autowired
    private VaccineRepository repository;

    /**
     * Valida los datos obligatorios de la vacuna.
     * @param vaccine Entidad a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(VaccineEntity vaccine) throws IllegalOperationException {
        if (vaccine == null) {
            throw new IllegalOperationException("La vacuna no puede ser nula.");
        }
        // Validamos que los meses de validez tengan sentido (ej. mayor a 0)
        if (vaccine.getValidityMonths() != null && vaccine.getValidityMonths() <= 0) {
            throw new IllegalOperationException("Los meses de validez de la vacuna deben ser mayores a cero.");
        }
    }

    /**
     * Crea una nueva vacuna en el sistema.
     * @param vaccine Entidad a crear.
     * @return La vacuna creada.
     * @throws IllegalOperationException Si no cumple las reglas.
     */
    @Transactional
    public VaccineEntity createVaccine(VaccineEntity vaccine) throws IllegalOperationException {
        log.info("Iniciando proceso de creación para una nueva vacuna");
        validateData(vaccine);
        if (vaccine == null) {
            throw new IllegalOperationException("La vacuna no puede ser nula.");
        }
        log.info("Vacuna creada exitosamente");
        return repository.save(vaccine);
    }

    /**
     * Obtiene todas las vacunas registradas.
     * @return Lista de vacunas.
     */
    @Transactional(readOnly = true)
    public List<VaccineEntity> getVaccines() {
        log.info("Consultando todas las vacunas");
        return repository.findAll();
    }

    /**
     * Obtiene una vacuna por su ID.
     * @param vaccineId ID de la vacuna.
     * @return La vacuna encontrada.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional(readOnly = true)
    public VaccineEntity getVaccine(Long vaccineId) throws EntityNotFoundException {
        log.info("Consultando vacuna con id = {}", vaccineId);
        return repository.findById(vaccineId)
                .orElseThrow(() -> {
                    log.error("Vacuna con id {} no encontrada", vaccineId);
                    return new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND);
                });
    }

    /**
     * Actualiza una vacuna existente.
     * @param vaccineId ID a actualizar.
     * @param vaccine Datos actualizados.
     * @return La vacuna actualizada.
     * @throws EntityNotFoundException Si no existe.
     * @throws IllegalOperationException Si no cumple las reglas.
     */
    @Transactional
    public VaccineEntity updateVaccine(Long vaccineId, VaccineEntity vaccine) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando actualización para la vacuna con id = {}", vaccineId);
        
        repository.findById(vaccineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND));
        
        validateData(vaccine);
        vaccine.setId(vaccineId);
        
        log.info("Vacuna con id = {} actualizada exitosamente", vaccineId);
        return repository.save(vaccine);
    }

    /**
     * Elimina una vacuna del sistema.
     * @param vaccineId ID a eliminar.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional
    public void deleteVaccine(Long vaccineId) throws EntityNotFoundException {
        log.info("Iniciando proceso de eliminación para vacuna ID: {}", vaccineId);
        
        if (!repository.existsById(vaccineId)) {
            log.error("Intento de eliminar vacuna inexistente con ID: {}", vaccineId);
            throw new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND);
        }
        
        repository.deleteById(vaccineId);
        log.info("Vacuna con ID: {} eliminada exitosamente", vaccineId);
    }
}