package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDetailDTO;
import co.edu.udistrital.mdp.pets.dto.MessageDTO;
import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.ReportDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterEventDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.EmailNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.InAppNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.SMSNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ModelMapper modelMapper;

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
	
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ShelterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        ShelterEntity entity = shelterService.getShelter(id);
        return modelMapper.map(entity, ShelterDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ShelterDTO create(@RequestBody ShelterDTO shelterDTO) throws IllegalOperationException {
        ShelterEntity entity = modelMapper.map(shelterDTO, ShelterEntity.class);
        ShelterEntity newEntity = shelterService.createShelter(entity);
        return modelMapper.map(newEntity, ShelterDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ShelterDTO update(@PathVariable Long id, @RequestBody ShelterDTO shelterDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = modelMapper.map(shelterDTO, ShelterEntity.class);
        ShelterEntity updatedEntity = shelterService.updateShelter(id, entity);
        return modelMapper.map(updatedEntity, ShelterDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        shelterService.deleteShelter(id);
    }

    @PostMapping(value = "/{id}/subscriptions/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable Long id, @PathVariable Long userId) throws EntityNotFoundException {
        shelterService.subscribeUser(id, userId);
    }

	/**
     * Obtiene los reportes asociados a un refugio.
     * Acceso: Generalmente restringido a Admin del refugio.
     */
    @GetMapping("/{id}/reports")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportDTO> getReports(@PathVariable Long id) throws EntityNotFoundException {
        ShelterEntity shelter = shelterService.getShelter(id);
        return modelMapper.map(shelter.getReports(), new TypeToken<List<ReportDTO>>() {}.getType());
    }

    /**
     * Obtiene los mensajes (buzón) de un refugio.
     * Acceso: Solo personal autorizado del refugio.
     */
    @GetMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> getMessages(@PathVariable Long id) throws EntityNotFoundException {
        ShelterEntity shelter = shelterService.getShelter(id);
        return modelMapper.map(shelter.getMessages(), new TypeToken<List<MessageDTO>>() {}.getType());
    }


	@PostMapping(value = "/{id}/notifications")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void notifySubscribers(
			@PathVariable Long id, 
			@RequestBody Map<String, Object> payload) throws EntityNotFoundException {
		
		String message = (String) payload.get("message");
		Map<String, Object> strategyData = (Map<String, Object>) payload.get("strategy");
		String type = (String) strategyData.get("type");

		NotificationStrategyEntity strategy;
		
		if ("EMAIL".equalsIgnoreCase(type)) {
			strategy = new EmailNotificationStrategyEntity(); 
		} else if ("SMS".equalsIgnoreCase(type)) {
			strategy = new SMSNotificationStrategyEntity();
		} else if ("InApp".equalsIgnoreCase(type)) {
			strategy = new InAppNotificationStrategyEntity();
		} else {
			throw new IllegalArgumentException("Unknown notification type: " + type);
		}

		modelMapper.map(strategyData, strategy);
		shelterService.notifyAllSubscribers(id, message, strategy);
	}

    @GetMapping("/{id}/events")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ShelterEventDTO> getEvents(@PathVariable Long id) throws EntityNotFoundException {
        List<ShelterEventEntity> events = shelterService.getShelter(id).getEvents();
        return modelMapper.map(events, new TypeToken<List<ShelterEventDTO>>() {}.getType());
    }

    @GetMapping("/{id}/pets")
    @ResponseStatus(code = HttpStatus.OK)
    public List<PetDTO> getPets(@PathVariable Long id) throws EntityNotFoundException {
        List<PetEntity> pets = shelterService.getShelter(id).getPets();
        return modelMapper.map(pets, new TypeToken<List<PetDTO>>() {}.getType());
    }

    @GetMapping("/{id}/veterinarians")
    @ResponseStatus(code = HttpStatus.OK)
    public List<VeterinarianDTO> getVeterinarians(@PathVariable Long id) throws EntityNotFoundException {
        List<VeterinarianEntity> veterinarians = shelterService.getShelter(id).getVeterinarians();
        return modelMapper.map(veterinarians, new TypeToken<List<VeterinarianDTO>>() {}.getType());
    }
}
