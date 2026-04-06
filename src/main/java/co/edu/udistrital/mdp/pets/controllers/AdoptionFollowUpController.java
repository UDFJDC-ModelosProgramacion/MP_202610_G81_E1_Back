package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionFollowUpDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionFollowUpService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow-ups")
public class AdoptionFollowUpController {

    @Autowired
    private AdoptionFollowUpService followUpService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieves all adoption follow-ups.
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdoptionFollowUpDTO> findAll() {
        List<AdoptionFollowUpEntity> followUps = followUpService.getFollowUps();
        return modelMapper.map(followUps, new TypeToken<List<AdoptionFollowUpDTO>>() {}.getType());
    }

    /**
     * Retrieves a specific follow-up by ID.
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdoptionFollowUpDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionFollowUpEntity entity = followUpService.getFollowUp(id);
        return modelMapper.map(entity, AdoptionFollowUpDetailDTO.class);
    }

    /**
     * Creates a new follow-up.
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public AdoptionFollowUpDTO create(@RequestBody AdoptionFollowUpDTO dto) throws IllegalOperationException {
        AdoptionFollowUpEntity entity = modelMapper.map(dto, AdoptionFollowUpEntity.class);
        AdoptionFollowUpEntity newEntity = followUpService.createFollowUp(entity);
        return modelMapper.map(newEntity, AdoptionFollowUpDTO.class);
    }

    /**
     * Updates an existing follow-up.
     * El parámetro isShelterOrAdmin simula la validación de rol requerida por tu service.
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdoptionFollowUpDTO update(
            @PathVariable Long id, 
            @RequestBody AdoptionFollowUpDTO dto,
            @RequestParam(defaultValue = "true") boolean isShelterOrAdmin) 
            throws EntityNotFoundException, IllegalOperationException {
        
        AdoptionFollowUpEntity entity = modelMapper.map(dto, AdoptionFollowUpEntity.class);
        AdoptionFollowUpEntity updatedEntity = followUpService.updateFollowUp(id, entity, isShelterOrAdmin);
        return modelMapper.map(updatedEntity, AdoptionFollowUpDTO.class);
    }

    /**
     * Deletes a follow-up.
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "true") boolean isShelterOrAdmin) 
            throws EntityNotFoundException, IllegalOperationException {
        followUpService.deleteFollowUp(id, isShelterOrAdmin);
    }
}
