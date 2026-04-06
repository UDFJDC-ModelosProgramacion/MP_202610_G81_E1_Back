package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdopterDTO;
import co.edu.udistrital.mdp.pets.dto.AdopterDetailDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequestDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdopterService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Adopter resources.
 *
 * Base URL: /adopters
 *
 * Endpoints:
 *   GET    /adopters           -> Returns all adopters
 *   GET    /adopters/{id}      -> Returns a specific adopter with details
 *   POST   /adopters           -> Creates a new adopter
 *   PUT    /adopters/{id}      -> Updates an existing adopter
 *   DELETE /adopters/{id}      -> Deletes an adopter (if no active requests/adoptions)
 *   GET    /adopters/{id}/adoptions  -> Returns adoptions of a specific adopter
 *   GET    /adopters/{id}/requests   -> Returns adoption requests of a specific adopter
 */
@RestController
@RequestMapping("/adopters")
public class AdopterController {

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /adopters
     * Retrieves all registered adopters.
     *
     * @return List of AdopterDTO with basic adopter information.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdopterDTO> findAll() {
        List<UserEntity> adopters = adopterService.getUsers();
        return modelMapper.map(adopters, new TypeToken<List<AdopterDTO>>() {}.getType());
    }

    /**
     * GET /adopters/{id}
     * Retrieves a specific adopter by their ID, including related adoptions and requests.
     *
     * @param id The ID of the adopter to retrieve.
     * @return AdopterDetailDTO with full adopter information and nested collections.
     * @throws EntityNotFoundException if no adopter with the given ID exists.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdopterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        UserEntity entity = adopterService.getUser(id);
        return modelMapper.map(entity, AdopterDetailDTO.class);
    }

    /**
     * POST /adopters
     * Creates a new adopter. Fields housingType, hasChildren, and hasOtherPets are mandatory.
     *
     * @param adopterDTO DTO containing the new adopter's data.
     * @return AdopterDTO of the newly created adopter.
     * @throws IllegalOperationException if business rules are violated (e.g., missing fields).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdopterDTO create(@RequestBody AdopterDTO adopterDTO) throws IllegalOperationException {
        AdopterEntity entity = modelMapper.map(adopterDTO, AdopterEntity.class);
        UserEntity created = adopterService.createUser(entity);
        return modelMapper.map(created, AdopterDTO.class);
    }

    /**
     * PUT /adopters/{id}
     * Updates an existing adopter's data.
     *
     * @param id         The ID of the adopter to update.
     * @param adopterDTO DTO with the updated adopter data.
     * @return AdopterDTO of the updated adopter.
     * @throws EntityNotFoundException   if no adopter with the given ID exists.
     * @throws IllegalOperationException if business rules are violated.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdopterDTO update(@PathVariable Long id, @RequestBody AdopterDTO adopterDTO)
            throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity entity = modelMapper.map(adopterDTO, AdopterEntity.class);
        UserEntity updated = adopterService.updateUser(id, entity);
        return modelMapper.map(updated, AdopterDTO.class);
    }

    /**
     * DELETE /adopters/{id}
     * Deletes an adopter by ID.
     * Not allowed if the adopter has pending adoption requests or registered adoptions.
     *
     * @param id The ID of the adopter to delete.
     * @throws EntityNotFoundException   if no adopter with the given ID exists.
     * @throws IllegalOperationException if the adopter has adoption records or active requests.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        adopterService.deleteUser(id);
    }

    /**
     * GET /adopters/{id}/adoptions
     * Retrieves all adoptions associated with a specific adopter.
     *
     * @param id The ID of the adopter.
     * @return List of AdoptionDTO linked to the adopter.
     * @throws EntityNotFoundException if no adopter with the given ID exists.
     */
    @GetMapping("/{id}/adoptions")
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionDTO> getAdoptions(@PathVariable Long id) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) adopterService.getUser(id);
        return modelMapper.map(adopter.getAdoptions(), new TypeToken<List<AdoptionDTO>>() {}.getType());
    }

    /**
     * GET /adopters/{id}/requests
     * Retrieves all adoption requests submitted by a specific adopter.
     *
     * @param id The ID of the adopter.
     * @return List of AdoptionRequestDTO linked to the adopter.
     * @throws EntityNotFoundException if no adopter with the given ID exists.
     */
    @GetMapping("/{id}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionRequestDTO> getAdoptionRequests(@PathVariable Long id) throws EntityNotFoundException {
        AdopterEntity adopter = (AdopterEntity) adopterService.getUser(id);
        return modelMapper.map(adopter.getAdoptionRequests(), new TypeToken<List<AdoptionRequestDTO>>() {}.getType());
    }
}
