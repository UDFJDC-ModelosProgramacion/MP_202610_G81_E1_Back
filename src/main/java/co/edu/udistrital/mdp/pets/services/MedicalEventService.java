package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MedicalEventRepository;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class MedicalEventService {

    @Autowired
    private MedicalEventRepository repository;
    @Autowired
    private MedicalHistoryRepository historyRepository;

    /**
     * Valida los datos obligatorios del evento médico.
     * @param event Entidad a validar.
     * @throws IllegalOperationException Si los datos no cumplen las reglas de negocio.
     */
    private void validateData(MedicalEventEntity event) throws IllegalOperationException {
        if (event == null) {
            throw new IllegalOperationException("El evento médico no puede ser nulo.");
        }
        if (event.getEventDate() != null && event.getEventDate().isAfter(LocalDate.now())) {
            throw new IllegalOperationException("La fecha del evento médico no puede ser en el futuro.");
        }
        if (event.getMedicalHistory() == null) {
            throw new IllegalOperationException("El evento debe estar asociado a una historia médica.");
        }
        Long historyId = event.getMedicalHistory().getId();
        if (historyId == null || !historyRepository.existsById(historyId)) {
            throw new IllegalOperationException("La historia médica asociada no existe.");
        }
    }

    /**
     * Crea un nuevo evento médico.
     * @param event Entidad a crear.
     * @return El evento creado.
     * @throws IllegalOperationException Si no cumple las reglas.
     */
    @Transactional
    public MedicalEventEntity createMedicalEvent(MedicalEventEntity event) throws IllegalOperationException {
        log.info("Iniciando proceso de creación para un evento médico");
        validateData(event);
        log.info("Evento médico creado exitosamente");
        return repository.save(event);
    }

    /**
     * Obtiene todos los eventos médicos.
     * @return Lista de eventos.
     */
    @Transactional(readOnly = true)
    public List<MedicalEventEntity> getMedicalEvents() {
        log.info("Consultando todos los eventos médicos");
        return repository.findAll();
    }

    /**
     * Obtiene un evento médico por su ID.
     * @param eventId ID del evento.
     * @return El evento encontrado.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional(readOnly = true)
    public MedicalEventEntity getMedicalEvent(Long eventId) throws EntityNotFoundException {
        log.info("Consultando evento médico con id = {}", eventId);
        return repository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Evento médico con id {} no encontrado", eventId);
                    return new EntityNotFoundException(ErrorMessage.MEDICAL_EVENT_NOT_FOUND);
                });
    }

    /**
     * Actualiza un evento médico existente.
     * @param eventId ID a actualizar.
     * @param event Datos actualizados.
     * @return El evento actualizado.
     * @throws EntityNotFoundException Si no existe.
     * @throws IllegalOperationException Si no cumple las reglas.
     */
    @Transactional
    public MedicalEventEntity updateMedicalEvent(Long eventId, MedicalEventEntity event) 
            throws EntityNotFoundException, IllegalOperationException {
        MedicalEventEntity persisted = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEDICAL_EVENT_NOT_FOUND));

        // impedir cambio de fecha
        if (event.getEventDate() != null && !event.getEventDate().equals(persisted.getEventDate())) {
            throw new IllegalOperationException("No se puede modificar la fecha de un evento ya creado.");
        }

        validateData(event);
        event.setId(eventId);
        return repository.save(event);
    }

    /**
     * Elimina un evento médico.
     * @param eventId ID a eliminar.
     * @throws EntityNotFoundException Si no existe.
     */
    @Transactional
    public void deleteMedicalEvent(Long eventId) throws EntityNotFoundException {
        log.info("Iniciando proceso de eliminación para evento médico ID: {}", eventId);
        
        if (!repository.existsById(eventId)) {
            log.error("Intento de eliminar evento médico inexistente con ID: {}", eventId);
            throw new EntityNotFoundException(ErrorMessage.MEDICAL_EVENT_NOT_FOUND);
        }
        
        repository.deleteById(eventId);
        log.info("Evento médico con ID: {} eliminado exitosamente", eventId);
    }
}