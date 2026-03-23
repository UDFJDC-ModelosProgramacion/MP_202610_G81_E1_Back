package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.repositories.AdoptionFollowUpRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdoptionFollowUpService {

    @Autowired
    private AdoptionFollowUpRepository repository;

    private void validateForCreate(AdoptionFollowUpEntity followUp) throws IllegalOperationException {
        if (followUp == null) {
            throw new IllegalOperationException("El seguimiento no puede ser nulo.");
        }
        if (followUp.getAdoption() == null) {
            throw new IllegalOperationException(ErrorMessage.ADOPTION_NOT_COMPLETED);
        }
        AdoptionEntity adoption = followUp.getAdoption();
        if (adoption.getStatus() == null || adoption.getStatus() != co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED) {
            throw new IllegalOperationException(ErrorMessage.ADOPTION_NOT_COMPLETED);
        }
        if (followUp.getFollowUpDate() == null) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_DATE_REQUIRED);
        }
        if (followUp.getNotes() == null || followUp.getNotes().trim().isEmpty()) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_NOTES_REQUIRED);
        }
    }

    @Transactional
    public AdoptionFollowUpEntity createFollowUp(AdoptionFollowUpEntity followUp) throws IllegalOperationException {
        log.info("Creando seguimiento de adopción");
        validateForCreate(followUp);
        return repository.save(followUp);
    }

    @Transactional(readOnly = true)
    public List<AdoptionFollowUpEntity> getFollowUps() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public AdoptionFollowUpEntity getFollowUp(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTION_FOLLOWUP_NOT_FOUND));
    }

    @Transactional
    public AdoptionFollowUpEntity updateFollowUp(Long id, AdoptionFollowUpEntity followUp, boolean isShelterOrAdmin)
            throws EntityNotFoundException, IllegalOperationException {

        if (!isShelterOrAdmin) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_PERMISSION_DENIED);
        }

        AdoptionFollowUpEntity existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTION_FOLLOWUP_NOT_FOUND));

        if (followUp.getFollowUpDate() == null) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_DATE_REQUIRED);
        }
        if (followUp.getNotes() == null || followUp.getNotes().trim().isEmpty()) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_NOTES_REQUIRED);
        }

        existing.setFollowUpDate(followUp.getFollowUpDate());
        existing.setNotes(followUp.getNotes());
        existing.setFrequency(followUp.getFrequency());
        existing.setVeterinarian(followUp.getVeterinarian());
        return repository.save(existing);
    }

    @Transactional
    public void deleteFollowUp(Long id, boolean isShelterOrAdmin) throws EntityNotFoundException, IllegalOperationException {
        if (!isShelterOrAdmin) {
            throw new IllegalOperationException(ErrorMessage.FOLLOWUP_PERMISSION_DENIED);
        }
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessage.ADOPTION_FOLLOWUP_NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AdoptionFollowUpEntity> findByAdoptionId(Long adoptionId) {
        return repository.findByAdoptionId(adoptionId);
    }

    @Transactional(readOnly = true)
    public List<AdoptionFollowUpEntity> findByVeterinarianId(Long veterinarianId) {
        return repository.findByVeterinarianId(veterinarianId);
    }

    @Transactional(readOnly = true)
    public List<AdoptionFollowUpEntity> findByFrequency(String frequency) {
        return repository.findByFrequency(frequency);
    }
}
