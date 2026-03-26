package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDetailDTO;
import co.edu.udistrital.mdp.pets.dto.UserAuthDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veterinarians")
public class VeterinarianController {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieves all veterinarians.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<VeterinarianDetailDTO> findAll() {

        List<UserEntity> users = veterinarianService.getUsers(); 
        return modelMapper.map(users, new TypeToken<List<VeterinarianDetailDTO>>() {}.getType());
    }

    /**
     * Retrieves a specific veterinarian by ID.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public VeterinarianDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
    
        VeterinarianEntity entity = (VeterinarianEntity) veterinarianService.getUser(id);
        return modelMapper.map(entity, VeterinarianDetailDTO.class);
    }

    /**
     * Creates a new veterinarian. 
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public VeterinarianDTO create(@RequestBody UserAuthDTO authDTO) throws IllegalOperationException, EntityNotFoundException {
   
        VeterinarianEntity entity = modelMapper.map(authDTO, VeterinarianEntity.class);
        VeterinarianEntity newEntity = (VeterinarianEntity) veterinarianService.createUser(entity);
        return modelMapper.map(newEntity, VeterinarianDTO.class);
    }

    /**
     * Updates veterinarian's professional info.
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public VeterinarianDTO update(@PathVariable Long id, @RequestBody VeterinarianDTO vetDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = modelMapper.map(vetDTO, VeterinarianEntity.class);
        VeterinarianEntity updatedEntity = (VeterinarianEntity) veterinarianService.updateUser(id, entity);
        return modelMapper.map(updatedEntity, VeterinarianDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        veterinarianService.deleteUser(id);
    }

    // --- NOTIFICATION ENDPOINTS ---

    @GetMapping(value = "/{id}/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDTO> getNotifications(@PathVariable Long id) throws EntityNotFoundException {
        List<NotificationEntity> notifications = veterinarianService.getNotifications(id);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDTO>>() {}.getType());
    }

    @PatchMapping(value = "/{id}/notifications/{notificationId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable Long id, @PathVariable Long notificationId) throws EntityNotFoundException {
        veterinarianService.markNotificationAsRead(id, notificationId);
    }
}
