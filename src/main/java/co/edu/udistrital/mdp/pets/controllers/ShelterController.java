package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDetailDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationStrategyDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ModelMapper modelMapper;

	/**
     * Retrieves all shelters.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ShelterDetailDTO> findAll(@RequestParam(required = false) String name) {
        List<ShelterEntity> shelters;
        if (name != null) {
            shelters = shelterService.findSheltersByName(name);
        } else {
            shelters = shelterService.getShelters();
        }
        return modelMapper.map(shelters, new TypeToken<List<ShelterDetailDTO>>() {}.getType());
    }
	
	/**
	 * Retrieves a specific pet by ID.
	 */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ShelterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ShelterEntity entity = shelterService.getShelter(id);
        return modelMapper.map(entity, ShelterDetailDTO.class);
    }

	/**
	 * Create a new shelter.
	 */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ShelterDTO create(@RequestBody ShelterDTO shelterDTO) throws IllegalOperationException {
        ShelterEntity entity = modelMapper.map(shelterDTO, ShelterEntity.class);
        ShelterEntity newEntity = shelterService.createShelter(entity);
        return modelMapper.map(newEntity, ShelterDTO.class);
    }

	/**
	 * Update an existing shelter.
	 */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ShelterDTO update(@PathVariable Long id, @RequestBody ShelterDTO shelterDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = modelMapper.map(shelterDTO, ShelterEntity.class);
        ShelterEntity updatedEntity = shelterService.updateShelter(id, entity);
        return modelMapper.map(updatedEntity, ShelterDTO.class);
    }

	/**
	 * Delete a shelter.
	 */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        shelterService.deleteShelter(id);
    }

    // --- ENDPOINTS DEL PATRON OBSERVER (Suscripciones y Notificaciones) ---

    /**
     * Suscribe un usuario al refugio.
     * POST /shelters/{id}/subscriptions/{userId}
     */
    @PostMapping(value = "/{id}/subscriptions/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable Long id, @PathVariable Long userId) throws EntityNotFoundException {
        shelterService.subscribeUser(id, userId);
    }

    /**
     * Envía una notificación masiva a todos los suscriptores.
     * POST /shelters/{id}/notifications
     */
    @PostMapping(value = "/{id}/notifications")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void notifySubscribers(
            @PathVariable Long id, 
            @RequestParam String message,
            @RequestBody NotificationStrategyDTO strategyDTO) throws EntityNotFoundException {
        
        // Convertimos el DTO de la estrategia (Email, SMS, etc.) a la entidad
        NotificationStrategyEntity strategy = modelMapper.map(strategyDTO, NotificationStrategyEntity.class);
        
        shelterService.notifyAllSubscribers(id, message, strategy);
    }
}
