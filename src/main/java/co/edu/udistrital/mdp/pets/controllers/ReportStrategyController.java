package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.entities.ReportStrategyEntity;
import co.edu.udistrital.mdp.pets.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report-strategies")
public class ReportStrategyController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReportStrategyEntity> findAll() {
        return reportService.getStrategies();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportStrategyEntity create(@RequestParam String type) {
        return reportService.createStrategy(type);
    }
}
