package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MedicalHistoryRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VaccinationRecordRepository;
import co.edu.udistrital.mdp.pets.repositories.VaccineRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VaccinationRecordService {

    @Autowired
    private VaccinationRecordRepository repository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MedicalHistoryRepository historyRepository;

    private void validateDates(VaccinationRecordEntity r) throws IllegalOperationException {
        if (r.getApplicationDate() == null || r.getNextDueDate() == null) {
            throw new IllegalOperationException("Las fechas de aplicación y vencimiento son obligatorias.");
        }
        if (r.getApplicationDate().isAfter(LocalDate.now())) {
            throw new IllegalOperationException("La fecha de aplicación no puede ser una fecha futura.");
        }
        if (!r.getNextDueDate().isAfter(r.getApplicationDate())) {
            throw new IllegalOperationException("La fecha de vencimiento debe ser posterior a la fecha de aplicación.");
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private PetEntity fetchPetOrThrow(Long petId) {
        try {
            return petRepository.findById(petId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private VaccineEntity fetchVaccineOrThrow(Long vaccineId) {
        try {
            return vaccineRepository.findById(vaccineId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINE_NOT_FOUND));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    private MedicalHistoryEntity fetchHistoryOrThrow(Long historyId) {
        try {
            return historyRepository.findById(historyId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MEDICAL_HISTORY_NOT_FOUND));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public VaccinationRecordEntity createVaccinationRecord(VaccinationRecordEntity record) throws IllegalOperationException {
        log.info("Iniciando el proceso de creación del registro de vacunación");
        if (record == null) throw new IllegalOperationException("El registro de vacunación no puede ser nulo.");

        validateDates(record);

        if (record.getPet() == null || record.getPet().getId() == null) {
            throw new IllegalOperationException("La mascota es obligatoria para el registro.");
        }
        PetEntity pet = fetchPetOrThrow(record.getPet().getId());
        record.setPet(pet);

        if (record.getVaccine() == null || record.getVaccine().getId() == null) {
            throw new IllegalOperationException("La vacuna es obligatoria para el registro.");
        }
        VaccineEntity vaccine = fetchVaccineOrThrow(record.getVaccine().getId());
        record.setVaccine(vaccine);

        // opcional: asociar medicalHistory si viene
        if (record.getMedicalHistory() != null && record.getMedicalHistory().getId() != null) {
            MedicalHistoryEntity history = fetchHistoryOrThrow(record.getMedicalHistory().getId());
            record.setMedicalHistory(history);
        }

        VaccinationRecordEntity saved = repository.save(record);
        log.info("Registro de vacunación creado id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<VaccinationRecordEntity> getVaccinationRecords() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public VaccinationRecordEntity getVaccinationRecord(Long recordId) throws EntityNotFoundException {
        return repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND));
    }

    @Transactional
    public VaccinationRecordEntity updateVaccinationRecord(Long recordId, VaccinationRecordEntity record)
            throws EntityNotFoundException, IllegalOperationException {

        VaccinationRecordEntity persisted = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND));

        // Validaciones de fechas (si vienen)
        if (record.getApplicationDate() != null && record.getApplicationDate().isAfter(LocalDate.now())) {
            throw new IllegalOperationException("La fecha de aplicación no puede ser una fecha futura.");
        }
        if (record.getApplicationDate() != null && record.getNextDueDate() != null
                && !record.getNextDueDate().isAfter(record.getApplicationDate())) {
            throw new IllegalOperationException("La fecha de vencimiento debe ser posterior a la fecha de aplicación.");
        }

        // Merge parcial
        if (record.getApplicationDate() != null) persisted.setApplicationDate(record.getApplicationDate());
        if (record.getNextDueDate() != null) persisted.setNextDueDate(record.getNextDueDate());
        if (record.getVaccinationDate() != null) persisted.setVaccinationDate(record.getVaccinationDate());

        if (record.getPet() != null && record.getPet().getId() != null) {
            PetEntity pet = fetchPetOrThrow(record.getPet().getId());
            persisted.setPet(pet);
        }

        if (record.getVaccine() != null && record.getVaccine().getId() != null) {
            VaccineEntity vaccine = fetchVaccineOrThrow(record.getVaccine().getId());
            persisted.setVaccine(vaccine);
        }

        if (record.getMedicalHistory() != null && record.getMedicalHistory().getId() != null) {
            MedicalHistoryEntity history = fetchHistoryOrThrow(record.getMedicalHistory().getId());
            persisted.setMedicalHistory(history);
        }

        VaccinationRecordEntity updated = repository.save(persisted);
        log.info("Registro de vacunación id={} actualizado", updated.getId());
        return updated;
    }

    @Transactional
    public void deleteVaccinationRecord(Long recordId) throws EntityNotFoundException {
        if (!repository.existsById(recordId)) {
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
        }
        repository.deleteById(recordId);
        log.info("Registro de vacunación id={} eliminado", recordId);
    }
}
