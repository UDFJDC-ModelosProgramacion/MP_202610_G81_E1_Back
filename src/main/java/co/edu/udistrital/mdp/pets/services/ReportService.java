package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ReportRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private ReportRepository repository;

    private void validateForCreate(ReportEntity report) throws IllegalOperationException {
        if (report == null) {
            throw new IllegalOperationException("El reporte no puede ser nulo.");
        }
        if (report.getReportedUser() == null) {
            throw new IllegalOperationException(ErrorMessage.REPORT_REPORTED_USER_REQUIRED);
        }
        if (report.getReason() == null || report.getReason().trim().isEmpty()) {
            throw new IllegalOperationException(ErrorMessage.REPORT_REASON_EMPTY);
        }
    }

    @Transactional
    public ReportEntity createReport(ReportEntity report) throws IllegalOperationException {
        log.info("Creando reporte");
        validateForCreate(report);
        report.setGenerateDate(LocalDate.now());
        report.setStatus(ReportEntity.Status.PENDING);
        return repository.save(report);
    }

    @Transactional(readOnly = true)
    public List<ReportEntity> getReports() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ReportEntity getReport(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REPORT_NOT_FOUND));
    }

    /**
     * Cambia el estado del reporte. Solo administradores pueden hacerlo.
     * @param id id del reporte
     * @param newStatus nuevo estado (PENDING, REVIEWED, RESOLVED)
     * @param isAdmin true si el actor es administrador
     */
    @Transactional
    public ReportEntity updateReportStatus(Long id, ReportEntity.Status newStatus, boolean isAdmin)
            throws EntityNotFoundException, IllegalOperationException {

        if (!isAdmin) {
            throw new IllegalOperationException(ErrorMessage.REPORT_PERMISSION_DENIED);
        }

        ReportEntity existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REPORT_NOT_FOUND));

        existing.setStatus(newStatus);
        return repository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<ReportEntity> findByGenerateDate(java.time.LocalDate date) {
        return repository.findByGenerateDate(date);
    }

    @Transactional(readOnly = true)
    public List<ReportEntity> findByReportStrategy(co.edu.udistrital.mdp.pets.entities.ReportStrategyEntity strategy) {
        return repository.findByReportStrategy(strategy);
    }
}
