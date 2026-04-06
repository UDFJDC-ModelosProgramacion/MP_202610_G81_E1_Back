package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.NotificationDetailDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationStrategyDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.NotificationService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification-strategies")
public class NotificationStrategyController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new notification strategy.
     * The type can be EMAIL, IN_APP, or SMS.
     */
    @PostMapping
	public NotificationDetailDTO createNotification(@RequestBody NotificationDetailDTO dto) throws IllegalOperationException {
    // 1. Mapeamos la notificación pero ignoramos la estrategia por un momento
    NotificationEntity entity = modelMapper.map(dto, NotificationEntity.class);

    // 2. Hidratación manual: Buscamos la estrategia REAL en la DB
    if (dto.getNotificationStrategy() != null) {
        try {
            NotificationStrategyEntity strategy = notificationService.getStrategy(dto.getNotificationStrategy().getId());
            entity.setNotificationStrategy(strategy);
        } catch (EntityNotFoundException e) {
            throw new IllegalOperationException("Strategy not found");
        }
    }

    return modelMapper.map(notificationService.createNotification(entity), NotificationDetailDTO.class);
	}

    /**
     * Retrieves all available notification strategies.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationStrategyDTO> getStrategies() {
        List<NotificationStrategyEntity> entities = notificationService.getStrategies();
        return modelMapper.map(entities, new TypeToken<List<NotificationStrategyDTO>>() {}.getType());
    }

    /**
     * Retrieves a specific strategy by ID.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationStrategyDTO getStrategy(@PathVariable Long id) 
            throws EntityNotFoundException {
        NotificationStrategyEntity entity = notificationService.getStrategy(id);
        return modelMapper.map(entity, NotificationStrategyDTO.class);
    }
}
