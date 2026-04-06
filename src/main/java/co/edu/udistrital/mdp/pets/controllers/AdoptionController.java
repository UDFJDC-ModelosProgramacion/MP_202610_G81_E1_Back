package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Adoption resources.
 *
 * Base URL: /adoptions
 *
 * Endpoints:
 *   GET    /adoptions       -> Returns all adoptions
 *   GET    /adoptions/{id}  -> Returns a specific adoption with full details
 *   POST   /adoptions       -> Creates a new adoption record
 *   PUT    /adoptions/{id}  -> Updates an adoption (adoptionDate is immutable once set)
 *   DELETE /adoptions/{id}  -> Deletes an adoption (not allowed if it has follow-ups)
 */
@RestController
@RequestMapping("/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /adoptions
     * Retrieves all adoption records in the system.
     *
     * @return List of AdoptionDTO with basic adoption information.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionDTO> findAll() {
        List<AdoptionEntity> adoptions = adoptionService.getAdoptions();
        return modelMapper.map(adoptions, new TypeToken<List<AdoptionDTO>>() {}.getType());
    }

    /**
     * GET /adoptions/{id}
     * Retrieves a specific adoption by its ID, including nested adopter, pet,
     * trial cohabitation, and follow-up records.
     *
     * @param id The ID of the adoption to retrieve.
     * @return AdoptionDetailDTO with full adoption details.
     * @throws EntityNotFoundException if no adoption with the given ID exists.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionEntity entity = adoptionService.getAdoption(id);
        return modelMapper.map(entity, AdoptionDetailDTO.class);
    }

    /**
     * POST /adoptions
     * Creates a new adoption record.
     * The adoptionDate field is required and cannot be empty.
     *
     * @param adoptionDTO DTO containing the adoption data to persist.
     * @return AdoptionDTO of the newly created adoption.
     * @throws IllegalOperationException if adoptionDate is null or other rules are violated.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionDTO create(@RequestBody AdoptionDTO adoptionDTO) throws IllegalOperationException {
        AdoptionEntity entity = modelMapper.map(adoptionDTO, AdoptionEntity.class);
        AdoptionEntity created = adoptionService.createAdoption(entity);
        return modelMapper.map(created, AdoptionDTO.class);
    }

    /**
     * PUT /adoptions/{id}
     * Updates an existing adoption.
     * Business rule: The adoptionDate cannot be changed once registered.
     *
     * @param id          The ID of the adoption to update.
     * @param adoptionDTO DTO with updated adoption data.
     * @return AdoptionDTO of the updated adoption.
     * @throws EntityNotFoundException   if no adoption with the given ID exists.
     * @throws IllegalOperationException if the adoptionDate is being modified.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionDTO update(@PathVariable Long id, @RequestBody AdoptionDTO adoptionDTO)
            throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = modelMapper.map(adoptionDTO, AdoptionEntity.class);
        AdoptionEntity updated = adoptionService.updateAdoption(id, entity);
        return modelMapper.map(updated, AdoptionDTO.class);
    }

    /**
     * DELETE /adoptions/{id}
     * Deletes an adoption by its ID.
     * Not allowed if the adoption has associated follow-up records.
     *
     * @param id The ID of the adoption to delete.
     * @throws EntityNotFoundException   if no adoption with the given ID exists.
     * @throws IllegalOperationException if the adoption has follow-ups associated.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        adoptionService.deleteAdoption(id);
    }
}
