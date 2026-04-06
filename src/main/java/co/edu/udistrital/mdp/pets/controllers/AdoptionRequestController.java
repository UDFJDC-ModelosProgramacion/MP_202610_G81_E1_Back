package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ApprovalStrategyEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionRequestService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/adoption-requests")
public class AdoptionRequestController {

    @Autowired
    private AdoptionRequestService requestService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionRequestDTO> findAll() {
        List<AdoptionRequestEntity> requests = requestService.getRequests();
        return modelMapper.map(requests, new TypeToken<List<AdoptionRequestDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionRequestDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionRequestEntity entity = requestService.getRequest(id);
        return modelMapper.map(entity, AdoptionRequestDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionRequestDTO create(@RequestBody AdoptionRequestDTO requestDTO) 
            throws IllegalOperationException, EntityNotFoundException {
        
        log.info("REST request to create AdoptionRequest for Pet ID: {}", requestDTO.getPetId());

        // Mapeo manual de IDs a entidades para evitar instanciar clases abstractas o nulas
        AdoptionRequestEntity request = new AdoptionRequestEntity();
        
        // 1. Setear Mascota
        if (requestDTO.getPetId() != null) {
            PetEntity pet = new PetEntity();
            pet.setId(requestDTO.getPetId());
            request.setPet(pet);
        }

        // 2. Setear Adoptante
        if (requestDTO.getAdopterId() != null) {
            AdopterEntity adopter = new AdopterEntity();
            adopter.setId(requestDTO.getAdopterId());
            request.setAdopter(adopter);
        }

        // 3. Setear Estrategia inicial (Opcional)
        if (requestDTO.getStrategyId() != null) {
            // Buscamos la estrategia real persistida en la DB
            List<ApprovalStrategyEntity> strategies = requestService.getStrategies();
            ApprovalStrategyEntity strategy = strategies.stream()
                .filter(s -> s.getId().equals(requestDTO.getStrategyId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Strategy not found"));
            request.setApprovalStrategy(strategy);
        }

        AdoptionRequestEntity newEntity = requestService.createRequest(request);
        return modelMapper.map(newEntity, AdoptionRequestDTO.class);
    }

    /**
     * Evalúa una solicitud existente usando una estrategia específica.
     */
    @PostMapping("/{id}/evaluate/{strategyId}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionRequestDTO evaluate(@PathVariable Long id, @PathVariable Long strategyId) 
            throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequestEntity updated = requestService.evaluateRequest(id, strategyId);
        return modelMapper.map(updated, AdoptionRequestDTO.class);
    }

    /**
     * Actualización manual de estado (Para administradores).
     */
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionRequestDTO updateStatus(@PathVariable Long id, @RequestParam String status) 
            throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequestEntity updated = requestService.updateRequestStatus(id, status);
        return modelMapper.map(updated, AdoptionRequestDTO.class);
    }
}
