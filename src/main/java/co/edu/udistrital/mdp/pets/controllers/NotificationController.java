package co.edu.udistrital.mdp.pets.controllers;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.NotificationService;
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDetailDTO createNotification(@RequestBody NotificationDetailDTO dto)
            throws IllegalOperationException {
        NotificationEntity entity = modelMapper.map(dto, NotificationEntity.class);
        return modelMapper.map(notificationService.createNotification(entity), NotificationDetailDTO.class);
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
    public NotificationDetailDTO updateNotification(@PathVariable Long id,
            @RequestBody NotificationDetailDTO dto)
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
