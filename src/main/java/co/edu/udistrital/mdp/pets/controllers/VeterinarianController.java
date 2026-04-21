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
    public ResponseEntity<List<VeterinarianDetailDTO>> findAll() {
        return ResponseEntity.ok(modelMapper.map(
            veterinarianService.getUsers(), 
            new TypeToken<List<VeterinarianDetailDTO>>() {}.getType()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarianDetailDTO> findOne(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            veterinarianService.getUser(id), 
            VeterinarianDetailDTO.class
        ));
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    public ResponseEntity<List<VaccinationRecordDetailDTO>> getVaccinations(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            veterinarianService.getVaccinationsEntities(id),
            new TypeToken<List<VaccinationRecordDetailDTO>>() {}.getType()
        ));
    }

    @GetMapping("/{id}/medical-events")
    public ResponseEntity<List<MedicalEventDetailDTO>> getMedicalEvents(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            veterinarianService.getMedicalEventsEntities(id),
            new TypeToken<List<MedicalEventDetailDTO>>() {}.getType()
        ));
    }

    @GetMapping("/{id}/follow-ups")
    public ResponseEntity<List<AdoptionFollowUpDetailDTO>> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            veterinarianService.getFollowUpsEntities(id), 
            new TypeToken<List<AdoptionFollowUpDetailDTO>>() {}.getType()
        ));
    }
}
