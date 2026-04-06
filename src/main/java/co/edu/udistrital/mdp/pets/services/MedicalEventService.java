package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
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

    private void validateBasicRules(MedicalEventEntity event) throws IllegalOperationException {
        if (event == null) {
            throw new IllegalOperationException("El evento médico no puede ser nulo.");
        }
        if (event.getEventDate() != null && event.getEventDate().isAfter(LocalDate.now())) {
            throw new IllegalOperationException("La fecha del evento médico no puede ser en el futuro.");
        }
    }

	private MedicalHistoryEntity fetchMedicalHistoryOrThrow(Long historyId) throws IllegalOperationException {
		return historyRepository.findById(historyId)
				.orElseThrow(() -> new IllegalOperationException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND));
	}

	@Transactional
	public MedicalEventEntity createMedicalEvent(MedicalEventEntity event) throws IllegalOperationException {
		log.info("Iniciando proceso de creación para un evento médico");
		validateBasicRules(event);

		if (event.getMedicalHistory() == null || event.getMedicalHistory().getId() == null) {
			throw new IllegalOperationException("El evento debe estar asociado a una historia médica.");
		}

		Long historyId = event.getMedicalHistory().getId();
		MedicalHistoryEntity history = fetchMedicalHistoryOrThrow(historyId);
		event.setMedicalHistory(history);

		MedicalEventEntity saved = repository.save(event);
		log.info("Evento médico creado exitosamente con id={}", saved.getId());
		return saved;
	}

    @Transactional(readOnly = true)
    public List<MedicalEventEntity> getMedicalEvents() {
        log.info("Consultando todos los eventos médicos");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public MedicalEventEntity getMedicalEvent(Long eventId) throws EntityNotFoundException {
        log.info("Consultando evento médico con id = {}", eventId);
        return repository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Evento médico con id {} no encontrado", eventId);
                    return new EntityNotFoundException(ErrorMessage.MEDICAL_EVENT_NOT_FOUND);
                });
    }

	@Transactional
	public MedicalEventEntity updateMedicalEvent(Long eventId, MedicalEventEntity event)
			throws EntityNotFoundException, IllegalOperationException {

		MedicalEventEntity persisted = repository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEDICAL_EVENT_NOT_FOUND));

		// Impedir cambio de fecha
		if (event.getEventDate() != null && !event.getEventDate().equals(persisted.getEventDate())) {
			throw new IllegalOperationException("No se puede modificar la fecha de un evento ya creado.");
		}

		validateBasicRules(event);

		// Validación estricta para el test: La historia es obligatoria en el update
		if (event.getMedicalHistory() == null || event.getMedicalHistory().getId() == null) {
			throw new IllegalOperationException("La historia médica asociada debe incluir un id.");
		}

		Long historyId = event.getMedicalHistory().getId();
		MedicalHistoryEntity history = fetchMedicalHistoryOrThrow(historyId);
		persisted.setMedicalHistory(history);

		// Merge parcial de campos
		if (event.getEventType() != null) persisted.setEventType(event.getEventType());
		if (event.getDiagnosis() != null) persisted.setDiagnosis(event.getDiagnosis());
		if (event.getDescription() != null) persisted.setDescription(event.getDescription());
		if (event.getTreatment() != null) persisted.setTreatment(event.getTreatment());

		log.info("Actualizando evento médico id={}", eventId);
		return repository.save(persisted);
	}

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

    @Transactional(readOnly = true)
    public List<MedicalEventEntity> getEventsByHistoryId(Long historyId) {
        if (!historyRepository.existsById(historyId)) {
            try {
                throw new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND);
            } catch (EntityNotFoundException ex) {
                System.getLogger(MedicalEventService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return repository.findByMedicalHistoryId(historyId);
    }
}
