package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
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

    /**
     * Retrieves all pets.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PetDetailDTO> findAll() {
        List<PetEntity> pets = petService.getPets();
        return modelMapper.map(pets, new TypeToken<List<PetDetailDTO>>() {}.getType());
    }

    /**
     * Retrieves a specific pet by ID.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity entity = petService.getPet(id);
        return modelMapper.map(entity, PetDetailDTO.class);
    }

    /**
     * Creates a new pet.
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetDTO create(@RequestBody PetDTO petDTO) throws IllegalOperationException {
        PetEntity entity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity newEntity = petService.createPet(entity);
        return modelMapper.map(newEntity, PetDTO.class);
    }

    /**
     * Updates an existing pet.
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDTO update(@PathVariable Long id, @RequestBody PetDTO petDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity entity = modelMapper.map(petDTO, PetEntity.class);
        PetEntity updatedEntity = petService.updatePet(id, entity);
        return modelMapper.map(updatedEntity, PetDTO.class);
    }

    /**
     * Deletes a pet.
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        petService.deletePet(id);
    }

    /**
     * Processes the return of an adopted pet to the shelter.
     * Custom business operation.
     */
    @PostMapping(value = "/{id}/returns")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDTO processReturn(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        PetEntity returnedPet = petService.processReturn(id);
        return modelMapper.map(returnedPet, PetDTO.class);
    }
}
