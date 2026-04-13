package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import co.edu.udistrital.mdp.pets.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ModelMapper modelMapper;
	@Autowired
	private UserRepository userRepository;

    /**
     * Creates a new notification without using ModelMapper for the abstract strategy.
     */
	@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDetailDTO createNotification(@RequestBody NotificationDetailDTO dto)
            throws IllegalOperationException, EntityNotFoundException {
        
        NotificationEntity entity = new NotificationEntity();
        entity.setMessage(dto.getMessage());
        entity.setDate(dto.getDate());
        entity.setIsRead(Boolean.TRUE.equals(dto.getIsRead()));

        // 1. Hidratar Estrategia
        if (dto.getNotificationStrategy() != null) {
            entity.setNotificationStrategy(notificationService.getStrategy(dto.getNotificationStrategy().getId()));
        }

        // 2. Hidratar Usuario (Buscando la instancia real de la DB)
        if (dto.getUserId() != null) {
            // Buscamos el Adopter o Veterinarian real para que JPA no chille con la clase abstracta
            co.edu.udistrital.mdp.pets.entities.UserEntity user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + dto.getUserId()));
            entity.setUser(user);
        }

        // 3. Llamar al createNotification normal que ya tenías en el Service
        NotificationEntity newEntity = notificationService.createNotification(entity);
        
        return modelMapper.map(newEntity, NotificationDetailDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getNotifications() {
        List<NotificationEntity> entities = notificationService.getNotifications();
        return modelMapper.map(entities, new TypeToken<List<NotificationDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDetailDTO getNotification(@PathVariable Long id)
            throws EntityNotFoundException {
        return modelMapper.map(notificationService.getNotification(id), NotificationDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDetailDTO updateNotification(@PathVariable Long id, @RequestBody NotificationDetailDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity entity = modelMapper.map(dto, NotificationEntity.class);
        return modelMapper.map(notificationService.updateNotification(id, entity), NotificationDetailDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) throws EntityNotFoundException {
        notificationService.deleteNotification(id);
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDetailDTO markAsRead(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(notificationService.markAsRead(id), NotificationDetailDTO.class);
    }
}
