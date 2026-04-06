package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDetailDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.VaccinationRecordDTO;
import co.edu.udistrital.mdp.pets.dto.MedicalEventDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
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

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<VeterinarianDTO> findAll() {
        List<UserEntity> allUsers = veterinarianService.getUsers(); 
        List<UserEntity> onlyVets = allUsers.stream()
            .filter(user -> user instanceof VeterinarianEntity)
            .toList();
        return modelMapper.map(onlyVets, new TypeToken<List<VeterinarianDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public VeterinarianDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        UserEntity user = veterinarianService.getUser(id);
        if (!(user instanceof VeterinarianEntity)) {
            throw new EntityNotFoundException("User with ID " + id + " is not a veterinarian.");
        }
        return modelMapper.map(user, VeterinarianDetailDTO.class);
    }

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public VeterinarianDTO create(@RequestBody VeterinarianDTO vetDTO) throws IllegalOperationException, EntityNotFoundException {
		VeterinarianEntity entity = modelMapper.map(vetDTO, VeterinarianEntity.class);
		UserEntity newEntity = veterinarianService.createUser(entity);
		return modelMapper.map(newEntity, VeterinarianDTO.class);
	}

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public VeterinarianDTO update(@PathVariable Long id, @RequestBody VeterinarianDTO vetDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = modelMapper.map(vetDTO, VeterinarianEntity.class);
        UserEntity updatedEntity = veterinarianService.updateUser(id, entity);
        return modelMapper.map(updatedEntity, VeterinarianDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        veterinarianService.deleteUser(id);
    }

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

    @GetMapping("/{id}/vaccinations")
    @ResponseStatus(code = HttpStatus.OK)
    public List<VaccinationRecordDTO> getVaccinations(@PathVariable Long id) throws EntityNotFoundException {
        VeterinarianEntity vet = (VeterinarianEntity) veterinarianService.getUser(id);
        return modelMapper.map(vet.getVaccinationRecords(), new TypeToken<List<VaccinationRecordDTO>>() {}.getType());
    }

    @GetMapping("/{id}/medical-events")
    @ResponseStatus(code = HttpStatus.OK)
    public List<MedicalEventDTO> getMedicalEvents(@PathVariable Long id) throws EntityNotFoundException {
        VeterinarianEntity vet = (VeterinarianEntity) veterinarianService.getUser(id);
        return modelMapper.map(vet.getMedicalEvents(), new TypeToken<List<MedicalEventDTO>>() {}.getType());
    }

    @GetMapping("/{id}/follow-ups")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionFollowUpDTO> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
        VeterinarianEntity vet = (VeterinarianEntity) veterinarianService.getUser(id);
        return modelMapper.map(vet.getAdoptionFollowUps(), new TypeToken<List<AdoptionFollowUpDTO>>() {}.getType());
    }
}
