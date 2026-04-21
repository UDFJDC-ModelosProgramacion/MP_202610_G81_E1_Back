package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.*;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<VeterinarianDTO>> findAll() {
        return ResponseEntity.ok(veterinarianService.findAllVets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarianDetailDTO> findOne(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(veterinarianService.getVetDetail(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarianDTO> create(@RequestBody VeterinarianDTO vetDTO) 
            throws IllegalOperationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarianService.createFromDTO(vetDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarianDTO> update(@PathVariable Long id, @RequestBody VeterinarianDTO vetDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        return ResponseEntity.ok(veterinarianService.updateFromDTO(id, vetDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        veterinarianService.deleteUser(id);
    }

    @GetMapping("/{id}/notifications")
	public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long id) throws EntityNotFoundException {
		return ResponseEntity.ok(modelMapper.map(
			veterinarianService.getNotifications(id),
			new TypeToken<List<NotificationDTO>>() {}.getType()
		));
	}

	@GetMapping("/{id}/vaccinations")
	public ResponseEntity<List<VaccinationRecordDTO>> getVaccinations(@PathVariable Long id) throws EntityNotFoundException {
		return ResponseEntity.ok(modelMapper.map(
			veterinarianService.getVaccinationsEntities(id),
			new TypeToken<List<VaccinationRecordDTO>>() {}.getType()
		));
	}

	@GetMapping("/{id}/medical-events")
	public ResponseEntity<List<MedicalEventDTO>> getMedicalEvents(@PathVariable Long id) throws EntityNotFoundException {
		return ResponseEntity.ok(modelMapper.map(
			veterinarianService.getMedicalEventsEntities(id),
			new TypeToken<List<MedicalEventDTO>>() {}.getType()
		));
	}

	@GetMapping("/{id}/follow-ups")
	public ResponseEntity<List<AdoptionFollowUpDTO>> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
		return ResponseEntity.ok(modelMapper.map(
			veterinarianService.getFollowUpsEntities(id), 
			new TypeToken<List<AdoptionFollowUpDTO>>() {}.getType()
		));
	}
}
