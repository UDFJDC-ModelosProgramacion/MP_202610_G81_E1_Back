package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository repository;

    @Autowired
    private PetRepository petRepository;

    private void validateData(MedicalHistoryEntity history) throws IllegalOperationException {
        if (history == null) {
            throw new IllegalOperationException("La historia clínica no puede ser nula.");
        }
        if (history.getPet() == null || history.getPet().getId() == null) {
            throw new IllegalOperationException("La historia clínica debe estar vinculada obligatoriamente a una mascota con id.");
        }
    }

    private PetEntity fetchPetOrThrow(Long petId) {
        try {
            return petRepository.findById(petId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));
        } catch (EntityNotFoundException e) {
            extracted(e);
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void extracted(EntityNotFoundException e) {
        e.printStackTrace();
    }

    @Transactional
    public MedicalHistoryEntity createMedicalHistory(MedicalHistoryEntity history) throws IllegalOperationException {
        log.info("Iniciando la creación de la historia clínica");
        validateData(history);

        Long petId = history.getPet().getId();

        PetEntity pet = fetchPetOrThrow(petId);
        history.setPet(pet);

        if (repository.existsByPetId(petId)) {
            throw new IllegalOperationException("La mascota ya tiene una historia clínica asignada.");
        }

        try {
            MedicalHistoryEntity saved = repository.save(history);
            log.info("Historia clínica creada exitosamente id={}", saved.getId());
            return saved;
        } catch (DataIntegrityViolationException dive) {
            log.error("Error de integridad al crear historia clínica para petId={}: {}", petId, dive.getMessage());
            throw new IllegalOperationException("No se pudo crear la historia clínica por restricción de integridad.");
        }
    }

    @Transactional(readOnly = true)
    public List<MedicalHistoryEntity> getMedicalHistories() {
        log.info("Consultando todas las historias clínicas");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public MedicalHistoryEntity getMedicalHistory(Long historyId) throws EntityNotFoundException {
        log.info("Consultando historia clínica con id = {}", historyId);
        return repository.findById(historyId)
                .orElseThrow(() -> {
                    log.error("Historia clínica con id {} no encontrada", historyId);
                    return new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND);
                });
    }

    @Transactional
    public MedicalHistoryEntity updateMedicalHistory(Long historyId, MedicalHistoryEntity history)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Iniciando actualización de historia clínica con id = {}", historyId);

        MedicalHistoryEntity persisted = repository.findById(historyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND));

        validateData(history);

        if (history.getPet() != null && history.getPet().getId() != null) {
            PetEntity pet = fetchPetOrThrow(history.getPet().getId());
            if (!pet.getId().equals(persisted.getPet().getId()) && repository.existsByPetId(pet.getId())) {
                throw new IllegalOperationException("La mascota especificada ya tiene una historia clínica.");
            }
            persisted.setPet(pet);
        }

        if (history.getDescription() != null) persisted.setDescription(history.getDescription());
        if (history.getLastCheckout() != null) persisted.setLastCheckout(history.getLastCheckout());
        if (history.getNotes() != null) persisted.setNotes(history.getNotes());

        MedicalHistoryEntity updated = repository.save(persisted);
        log.info("Historia clínica con id = {} actualizada exitosamente", updated.getId());
        return updated;
    }

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
