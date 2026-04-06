package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdopterDTO;
import co.edu.udistrital.mdp.pets.dto.AdopterDetailDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.services.AdopterService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adopters")
public class AdopterController {

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdopterDTO> findAll() {
        List<UserEntity> allUsers = adopterService.getUsers(); 
        List<UserEntity> onlyAdopters = allUsers.stream()
            .filter(user -> user instanceof AdopterEntity)
            .toList();
        return modelMapper.map(onlyAdopters, new TypeToken<List<AdopterDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdopterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        UserEntity user = adopterService.getUser(id);
        if (!(user instanceof AdopterEntity)) {
            throw new EntityNotFoundException("User with ID " + id + " is not an adopter.");
        }
        return modelMapper.map(user, AdopterDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public AdopterDTO create(@RequestBody AdopterDTO adopterDTO) throws IllegalOperationException, EntityNotFoundException {
        AdopterEntity entity = modelMapper.map(adopterDTO, AdopterEntity.class);
        UserEntity newEntity = adopterService.createUser(entity);
        return modelMapper.map(newEntity, AdopterDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdopterDTO update(@PathVariable Long id, @RequestBody AdopterDTO adopterDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = modelMapper.map(adopterDTO, AdopterEntity.class);
        UserEntity updatedEntity = adopterService.updateUser(id, entity);
        return modelMapper.map(updatedEntity, AdopterDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        adopterService.deleteUser(id);
    }

    @GetMapping(value = "/{id}/notifications")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificationDTO> getNotifications(@PathVariable Long id) throws EntityNotFoundException {
        List<NotificationEntity> notifications = adopterService.getNotifications(id);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDTO>>() {}.getType());
    }

    @PatchMapping(value = "/{id}/notifications/{notificationId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable Long id, @PathVariable Long notificationId) throws EntityNotFoundException {
        adopterService.markNotificationAsRead(id, notificationId);
    }

    @GetMapping("/{id}/adoptions")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionDTO> getAdoptions(@PathVariable Long id) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) adopterService.getUser(id);
        return modelMapper.map(adopter.getAdoptions(), new TypeToken<List<AdoptionDTO>>() {}.getType());
    }

    @GetMapping("/{id}/adoption-requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionRequestDTO> getAdoptionRequests(@PathVariable Long id) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) adopterService.getUser(id);
        return modelMapper.map(adopter.getAdoptionRequests(), new TypeToken<List<AdoptionRequestDTO>>() {}.getType());
    }

    @GetMapping("/{id}/reviews")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDTO> getReviews(@PathVariable Long id) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) adopterService.getUser(id);
        return modelMapper.map(adopter.getReviews(), new TypeToken<List<ReviewDTO>>() {}.getType());
    }
}
