package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.ReportDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.ReportEntity;
import co.edu.udistrital.mdp.pets.entities.ReportStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReportDTO> findAll() {
        List<ReportEntity> entities = reportService.getReports();
        return modelMapper.map(entities, new TypeToken<List<ReportDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReportDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ReportEntity entity = reportService.getReport(id);
        return modelMapper.map(entity, ReportDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDTO create(@RequestBody ReportDTO reportDTO) throws IllegalOperationException, EntityNotFoundException {
        log.info("Creando reporte desde REST: {}", reportDTO.getReason());

        // 1. Instanciamos la entidad base
        ReportEntity report = new ReportEntity();
        report.setReason(reportDTO.getReason());

        // 2. Relación con Shelter (ID)
        if (reportDTO.getShelterId() != null) {
            ShelterEntity shelter = new ShelterEntity();
            shelter.setId(reportDTO.getShelterId());
            report.setShelter(shelter);
        }

        // 3. Relación con ReportedUser (Fix: Usamos AdopterEntity porque UserEntity es abstracta)
        if (reportDTO.getReportedUserId() != null) {
            AdopterEntity reported = new AdopterEntity();
            reported.setId(reportDTO.getReportedUserId());
            report.setReportedUser(reported);
        }

        // 4. Relación con la Estrategia (Buscamos la entidad real vía Service)
        if (reportDTO.getStrategyId() != null) {
            // Usamos el método que centralizamos en el ReportService
            ReportStrategyEntity strategy = reportService.getStrategy(reportDTO.getStrategyId());
            report.setReportStrategy(strategy);
        }

        ReportEntity saved = reportService.createReport(report);
        return modelMapper.map(saved, ReportDTO.class);
    }

    @PutMapping("/{id}/strategy/{strategyId}")
    @ResponseStatus(HttpStatus.OK)
    public ReportDTO assignStrategy(@PathVariable Long id, @PathVariable Long strategyId) 
            throws EntityNotFoundException {
        ReportEntity updated = reportService.assignStrategy(id, strategyId);
        return modelMapper.map(updated, ReportDTO.class);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ReportDTO updateStatus(
            @PathVariable Long id, 
            @RequestParam ReportEntity.Status status,
            @RequestParam(defaultValue = "false") boolean isAdmin) 
            throws EntityNotFoundException, IllegalOperationException {
        ReportEntity updated = reportService.updateReportStatus(id, status, isAdmin);
        return modelMapper.map(updated, ReportDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        reportService.deleteReport(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportDTO> findByDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<ReportEntity> entities = reportService.findByGenerateDate(localDate);
        return modelMapper.map(entities, new TypeToken<List<ReportDTO>>() {}.getType());
    }
}
