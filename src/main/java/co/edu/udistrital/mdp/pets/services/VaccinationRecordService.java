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
    public VaccinationRecordEntity createVaccinationRecord(VaccinationRecordEntity vaccinationRecord) throws IllegalOperationException {
        log.info("Iniciando el proceso de creación del registro de vacunación");
        if (vaccinationRecord == null) throw new IllegalOperationException("El registro de vacunación no puede ser nulo.");

        validateDates(vaccinationRecord);

        if (vaccinationRecord.getPet() == null || vaccinationRecord.getPet().getId() == null) {
            throw new IllegalOperationException("La mascota es obligatoria para el registro.");
        }
        PetEntity pet = fetchPetOrThrow(vaccinationRecord.getPet().getId());
        vaccinationRecord.setPet(pet);

        if (vaccinationRecord.getVaccine() == null || vaccinationRecord.getVaccine().getId() == null) {
            throw new IllegalOperationException("La vacuna es obligatoria para el registro.");
        }
        VaccineEntity vaccine = fetchVaccineOrThrow(vaccinationRecord.getVaccine().getId());
        vaccinationRecord.setVaccine(vaccine);

        // opcional: asociar medicalHistory si viene
        if (vaccinationRecord.getMedicalHistory() != null && vaccinationRecord.getMedicalHistory().getId() != null) {
            MedicalHistoryEntity history = fetchHistoryOrThrow(vaccinationRecord.getMedicalHistory().getId());
            vaccinationRecord.setMedicalHistory(history);
        }

        VaccinationRecordEntity saved = repository.save(vaccinationRecord);
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
    public VaccinationRecordEntity updateVaccinationRecord(Long recordId, VaccinationRecordEntity vaccinationRecord)
            throws EntityNotFoundException, IllegalOperationException {

        VaccinationRecordEntity persisted = repository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND));

        // Validaciones de fechas (si vienen)
        if (vaccinationRecord.getApplicationDate() != null && vaccinationRecord.getApplicationDate().isAfter(LocalDate.now())) {
            throw new IllegalOperationException("La fecha de aplicación no puede ser una fecha futura.");
        }
        if (vaccinationRecord.getApplicationDate() != null && vaccinationRecord.getNextDueDate() != null
                && !vaccinationRecord.getNextDueDate().isAfter(vaccinationRecord.getApplicationDate())) {
            throw new IllegalOperationException("La fecha de vencimiento debe ser posterior a la fecha de aplicación.");
        }

        // Merge parcial
        if (vaccinationRecord.getApplicationDate() != null) persisted.setApplicationDate(vaccinationRecord.getApplicationDate());
        if (vaccinationRecord.getNextDueDate() != null) persisted.setNextDueDate(vaccinationRecord.getNextDueDate());
        if (vaccinationRecord.getVaccinationDate() != null) persisted.setVaccinationDate(vaccinationRecord.getVaccinationDate());

        if (vaccinationRecord.getPet() != null && vaccinationRecord.getPet().getId() != null) {
            PetEntity pet = fetchPetOrThrow(vaccinationRecord.getPet().getId());
            persisted.setPet(pet);
        }

        if (vaccinationRecord.getVaccine() != null && vaccinationRecord.getVaccine().getId() != null) {
            VaccineEntity vaccine = fetchVaccineOrThrow(vaccinationRecord.getVaccine().getId());
            persisted.setVaccine(vaccine);
        }

        if (vaccinationRecord.getMedicalHistory() != null && vaccinationRecord.getMedicalHistory().getId() != null) {
            MedicalHistoryEntity history = fetchHistoryOrThrow(vaccinationRecord.getMedicalHistory().getId());
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
