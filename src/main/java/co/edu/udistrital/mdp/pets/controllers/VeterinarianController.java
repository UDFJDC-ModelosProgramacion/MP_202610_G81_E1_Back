package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.*;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

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
        return ResponseEntity.ok(veterinarianService.getNotificationsDTO(id));
    }

    @PatchMapping("/{id}/notifications/{notificationId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void markAsRead(@PathVariable Long id, @PathVariable Long notificationId) throws EntityNotFoundException {
        veterinarianService.markNotificationAsRead(id, notificationId);
    }

    @GetMapping("/{id}/vaccinations")
    public ResponseEntity<List<VaccinationRecordDTO>> getVaccinations(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(veterinarianService.getVaccinationsByVet(id));
    }

    @GetMapping("/{id}/medical-events")
    public ResponseEntity<List<MedicalEventDTO>> getMedicalEvents(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(veterinarianService.getMedicalEventsByVet(id));
    }

    @GetMapping("/{id}/follow-ups")
    public ResponseEntity<List<AdoptionFollowUpDTO>> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(veterinarianService.getFollowUpsByVet(id));
    }
}
