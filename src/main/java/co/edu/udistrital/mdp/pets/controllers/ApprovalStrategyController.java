package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.entities.ApprovalStrategyEntity;
import co.edu.udistrital.mdp.pets.services.AdoptionRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/approval-strategies")
public class ApprovalStrategyController {

    @Autowired
    private AdoptionRequestService requestService;

    /**
     * Obtiene la lista de todas las estrategias de aprobación configuradas.
     * @return Lista de ApprovalStrategyEntity (Manual, Medical, Score).
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ApprovalStrategyEntity> findAll() {
        log.info("Consultando todas las estrategias de aprobación");
        return requestService.getStrategies();
    }

    /**
     * Crea una nueva estrategia de aprobación según el tipo.
     * @param type Tipo de estrategia: MANUAL, MEDICAL o SCORE.
     * @return La entidad de la estrategia creada y persistida.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalStrategyEntity create(@RequestParam String type) {
        log.info("Creando nueva estrategia de aprobación de tipo: {}", type);
        return requestService.createStrategy(type);
    }
}
