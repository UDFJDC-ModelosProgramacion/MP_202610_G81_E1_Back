package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.repositories.ShelterEventRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShelterEventService {

    @Autowired
    private ShelterEventRepository shelterEventRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private void validateShelterEvent(ShelterEventEntity event) throws IllegalOperationException {
        if (event == null)
            throw new IllegalOperationException("ShelterEvent data cannot be null");

        if (event.getTitle() == null || event.getTitle().trim().isEmpty())
            throw new IllegalOperationException("Event title cannot be empty");

        if (event.getDate() == null)
            throw new IllegalOperationException("Event date cannot be null");

        if (event.getLocation() == null || event.getLocation().trim().isEmpty())
            throw new IllegalOperationException("Event location cannot be empty");

        if (event.getShelter() == null || event.getShelter().getId() == null)
            throw new IllegalOperationException("Event must belong to a shelter");

        shelterRepository.findById(event.getShelter().getId())
                .orElseThrow(() -> new IllegalOperationException("Shelter does not exist"));
    }

    @Transactional
    public ShelterEventEntity createShelterEvent(ShelterEventEntity event)
            throws IllegalOperationException {
        log.info("Creating shelter event: {}", event.getTitle());

        validateShelterEvent(event);

        if (event.getStatus() == null) {
            event.setStatus(ProcessStatus.IN_PROGRESS);
        }

        return shelterEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<ShelterEventEntity> getShelterEvents() {
        return shelterEventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ShelterEventEntity getShelterEvent(Long eventId) throws EntityNotFoundException {
        return shelterEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_EVENT_NOT_FOUND));
    }

    @Transactional
    public ShelterEventEntity updateShelterEvent(Long eventId, ShelterEventEntity updatedEvent)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating shelter event with id = {}", eventId);

        ShelterEventEntity existing = shelterEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_EVENT_NOT_FOUND));

        validateShelterEvent(updatedEvent);

        if (existing.getStatus() == ProcessStatus.FINISHED) {
            throw new IllegalOperationException("Cannot update a finished event");
        }

        updatedEvent.setId(eventId);
        return shelterEventRepository.save(updatedEvent);
    }

    @Transactional
    public void deleteShelterEvent(Long eventId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting shelter event with id = {}", eventId);

        ShelterEventEntity event = shelterEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_EVENT_NOT_FOUND));

        if (event.getStatus() != ProcessStatus.FINISHED) {
            throw new IllegalOperationException(
                    "Cannot delete a shelter event that is not finished yet");
        }

        shelterEventRepository.deleteById(eventId);
    }

    @Transactional
    public ShelterEventEntity finishEvent(Long eventId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Finishing shelter event with id = {}", eventId);

        ShelterEventEntity event = shelterEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_EVENT_NOT_FOUND));

        if (event.getStatus() == ProcessStatus.FINISHED) {
            throw new IllegalOperationException("Event is already finished");
        }

        event.setStatus(ProcessStatus.FINISHED);
        return shelterEventRepository.save(event);
    }
}
