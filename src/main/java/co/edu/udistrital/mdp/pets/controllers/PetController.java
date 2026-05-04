package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.services.PetService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<PetDTO> findAll(
        @RequestParam(required = false) String species,
        @RequestParam(required = false) String size,
        @RequestParam(required = false) PetStatus status) {
    
    List<PetEntity> pets = petService.getPets(species, size, status);
    return modelMapper.map(pets, new TypeToken<List<PetDTO>>() {}.getType());
	}

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity entity = petService.getPet(id);
        return modelMapper.map(entity, PetDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetDTO create(@RequestBody PetDTO petDTO) throws IllegalOperationException {
        PetEntity entity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity newEntity = petService.createPet(entity);
        return modelMapper.map(newEntity, PetDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDTO update(@PathVariable Long id, @RequestBody PetDTO petDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity entity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity updatedEntity = petService.updatePet(id, entity);
        return modelMapper.map(updatedEntity, PetDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        petService.deletePet(id);
    }

    @PostMapping(value = "/{id}/returns")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDTO processReturn(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        PetEntity returnedPet = petService.processReturn(id);
        return modelMapper.map(returnedPet, PetDTO.class);
    }

    @GetMapping("/{id}/adoptions")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionDTO> getAdoptions(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet.getAdoptions(), new TypeToken<List<AdoptionDTO>>() {}.getType());
    }

    @GetMapping("/{id}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionRequestDTO> getRequests(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet.getAdoptionRequests(), new TypeToken<List<AdoptionRequestDTO>>() {}.getType());
    }

    @GetMapping("/{id}/follow-ups")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionFollowUpDTO> getFollowUps(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet.getFollowUps(), new TypeToken<List<AdoptionFollowUpDTO>>() {}.getType());
    }

    @GetMapping("/{id}/reviews")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReviewDTO> getReviews(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet.getReviews(), new TypeToken<List<ReviewDTO>>() {}.getType());
    }

    @GetMapping("/{id}/trials")
    @ResponseStatus(code = HttpStatus.OK)
    public List<TrialCohabitationDTO> getTrials(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity pet = petService.getPet(id);
        return modelMapper.map(pet.getTrials(), new TypeToken<List<TrialCohabitationDTO>>() {}.getType());
    }
}
