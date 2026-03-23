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
import co.edu.udistrital.mdp.pets.repositories.VaccineRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class VaccinationRecordService {

	@Autowired
	private VaccineRepository vaccineRepository;

    @Autowired
    private VaccinationRecordRepository repository;

	private void validateData(VaccinationRecordEntity vaccinationRecord) throws IllegalOperationException {
        if (vaccinationRecord == null) {
            throw new IllegalOperationException("El registro de vacunación no puede ser nulo.");
        }
        
        // 1. Validar fechas obligatorias
        if (vaccinationRecord.getApplicationDate() == null || vaccinationRecord.getNextDueDate() == null) {
            throw new IllegalOperationException("Las fechas de aplicación y vencimiento son obligatorias.");
        }

        // 2. Validar que la fecha de aplicación no sea futura (Test: testCreateRecordWithFutureDateFails)
        if (vaccinationRecord.getApplicationDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalOperationException("La fecha de aplicación no puede ser una fecha futura.");
        }

        // 3. Validar coherencia de fechas
        if (vaccinationRecord.getNextDueDate().isBefore(vaccinationRecord.getApplicationDate())) {
            throw new IllegalOperationException("La fecha de vencimiento no puede ser anterior a la aplicación.");
        }

        // 4. Validar Mascota obligatoria (Test: testCreateRecordWithNullPetFails)
        if (vaccinationRecord.getPet() == null) {
            throw new IllegalOperationException("La mascota es obligatoria para el registro.");
        }

        // 5. Validar Vacuna obligatoria (Test: testCreateRecordWithNullVaccineFails)
        if (vaccinationRecord.getVaccine() == null) {
            throw new IllegalOperationException("La vacuna es obligatoria para el registro.");
        }

        // 6. Validar que la vacuna exista en DB (Test: testCreateRecordWithNonexistentVaccineFails)
        // Esto evita el DataIntegrityViolationException y lanza la excepción de negocio correcta
		if (vaccinationRecord.getVaccine().getId() == null || !vaccineRepository.existsById(vaccinationRecord.getVaccine().getId())) {
            throw new IllegalOperationException("La vacuna especificada no existe.");
        }
    }

    @Transactional
    public VaccinationRecordEntity createVaccinationRecord(VaccinationRecordEntity vaccinationRecord) throws IllegalOperationException {
        log.info("Iniciando el proceso de creación del registro de vacunación");
        validateData(vaccinationRecord);
        return repository.save(vaccinationRecord);
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
        
        if (!repository.existsById(recordId)) {
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
        }
        
        validateData(vaccinationRecord);
        vaccinationRecord.setId(recordId);
        
        return repository.save(vaccinationRecord);
    }

    @Transactional
    public void deleteVaccinationRecord(Long recordId) throws EntityNotFoundException {
        if (!repository.existsById(recordId)) {
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_NOT_FOUND);
        }
        repository.deleteById(recordId);
    }
}
