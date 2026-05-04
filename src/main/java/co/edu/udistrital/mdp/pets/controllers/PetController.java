package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDTO;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.services.PetService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<PetDTO>> findAll(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) PetStatus status) {
        return ResponseEntity.ok(petService.findAllDTOs(species, size, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDetailDTO> findOne(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findDetailDTO(id));
    }

    @PostMapping
    public ResponseEntity<PetDTO> create(@RequestBody PetDTO petDTO) throws IllegalOperationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createFromDTO(petDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody PetDTO petDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        return ResponseEntity.ok(petService.updateFromDTO(id, petDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        petService.deletePet(id);
    }

    @PostMapping("/{id}/returns")
    public ResponseEntity<PetDTO> processReturn(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        return ResponseEntity.ok(petService.processReturnDTO(id));
    }

    @GetMapping("/{id}/adoptions")
    public ResponseEntity<List<AdoptionDTO>> getAdoptions(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findAdoptionsByPetId(id));
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<List<AdoptionRequestDTO>> getRequests(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findRequestsByPetId(id));
    }

    @GetMapping("/{id}/follow-ups")
    public ResponseEntity<List<AdoptionFollowUpDTO>> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findFollowUpsByPetId(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findReviewsByPetId(id));
    }

    @GetMapping("/{id}/trials")
    public ResponseEntity<List<TrialCohabitationDTO>> getTrials(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(petService.findTrialsByPetId(id));
    }
}
