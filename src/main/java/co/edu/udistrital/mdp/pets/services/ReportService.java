package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.entities.ReportStrategyEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ReportRepository;
import co.edu.udistrital.mdp.pets.repositories.ReportStrategyRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private ReportRepository repository;

    @Autowired
    private ReportStrategyRepository strategyRepository; // Inyección necesaria

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

        // EJECUCIÓN DEL PATRÓN STRATEGY:
        // Si el reporte ya trae una estrategia asignada, ejecutamos su lógica
        if (report.getReportStrategy() != null) {
            report.getReportStrategy().generate(report);
        }

        return repository.save(report);
    }

    /**
     * Permite asignar una estrategia a un reporte existente y ejecutarla.
     */
    @Transactional
    public ReportEntity assignStrategy(Long reportId, Long strategyId) 
            throws EntityNotFoundException {
        ReportEntity report = getReport(reportId);
        ReportStrategyEntity strategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException("Estrategia no encontrada"));

        report.setReportStrategy(strategy);
        strategy.generate(report); // Se ejecuta la lógica polimórfica
        
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

	/**
     * Elimina un reporte del sistema.
     * @param id Identificador del reporte a eliminar.
     * @throws EntityNotFoundException Si el reporte no existe.
     */
    @Transactional
    public void deleteReport(Long id) throws EntityNotFoundException {
        log.info("Eliminando reporte con id: {}", id);
        ReportEntity report = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REPORT_NOT_FOUND));
        
        repository.delete(report);
    }
}
