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
public class VaccineService {

    @Autowired
    private VaccineRepository repository;

    private void validateVaccine(VaccineEntity vaccine) throws IllegalOperationException {
        if (vaccine == null) {
            throw new IllegalOperationException("Vaccine cannot be null.");
        }
        if (vaccine.getName() == null || vaccine.getName().trim().isEmpty()) {
            throw new IllegalOperationException("El nombre de la vacuna no puede estar vacío.");
        }
        if (vaccine.getValidityMonths() == null || vaccine.getValidityMonths() <= 0) {
            throw new IllegalOperationException("La validez debe ser mayor a 0 meses.");
        }
    }

    @Transactional
    public VaccineEntity createVaccine(VaccineEntity vaccine) throws IllegalOperationException {
        validateVaccine(vaccine);
        VaccineEntity saved = repository.save(vaccine);
        log.info("Vacuna creada id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public VaccineEntity getVaccine(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<VaccineEntity> getVaccines() {
        return repository.findAll();
    }

    @Transactional
    public VaccineEntity updateVaccine(Long id, VaccineEntity vaccine) throws EntityNotFoundException, IllegalOperationException {
        VaccineEntity persisted = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND));

        validateVaccine(vaccine);

        // Merge parcial
        if (vaccine.getName() != null) persisted.setName(vaccine.getName());
        if (vaccine.getDescription() != null) persisted.setDescription(vaccine.getDescription());
        if (vaccine.getValidityMonths() != null) persisted.setValidityMonths(vaccine.getValidityMonths());

        VaccineEntity updated = repository.save(persisted);
        log.info("Vacuna id={} actualizada", updated.getId());
        return updated;
    }

    @Transactional
    public void deleteVaccine(Long id) throws EntityNotFoundException {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND);
        }
        repository.deleteById(id);
        log.info("Vacuna id={} eliminada", id);
    }
}
