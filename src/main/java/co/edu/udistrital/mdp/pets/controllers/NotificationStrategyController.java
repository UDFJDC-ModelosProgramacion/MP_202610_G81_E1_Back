package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.NotificationStrategyDTO;
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
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationStrategyDTO createStrategy(@RequestParam String type) 
            throws IllegalOperationException {
        // Notamos que no enviamos Body porque BaseEntity solo tiene ID
        NotificationStrategyEntity entity = notificationService.createStrategy(type);
        return modelMapper.map(entity, NotificationStrategyDTO.class);
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
