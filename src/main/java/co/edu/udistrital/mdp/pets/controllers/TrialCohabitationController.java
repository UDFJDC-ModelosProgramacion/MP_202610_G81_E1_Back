package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDTO;
import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.TrialCohabitationService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing TrialCohabitation resources.
 *
 * A TrialCohabitation represents the test period a pet lives with a potential adopter
 * before the adoption is finalized.
 *
 * Base URL: /trial-cohabitations
 *
 * Endpoints:
 *   GET    /trial-cohabitations       -> Returns all trial cohabitations
 *   GET    /trial-cohabitations/{id}  -> Returns a specific trial cohabitation with details
 *   POST   /trial-cohabitations       -> Creates a new trial cohabitation
 *   PUT    /trial-cohabitations/{id}  -> Updates a trial cohabitation
 *   DELETE /trial-cohabitations/{id}  -> Deletes a trial cohabitation (not allowed if in progress)
 */
@RestController
@RequestMapping("/trial-cohabitations")
public class TrialCohabitationController {

    @Autowired
    private TrialCohabitationService trialCohabitationService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /trial-cohabitations
     * Retrieves all trial cohabitation records in the system.
     *
     * @return List of TrialCohabitationDTO with basic information.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrialCohabitationDTO> findAll() {
        List<TrialCohabitationEntity> trials = trialCohabitationService.getTrialCohabitations();
        return modelMapper.map(trials, new TypeToken<List<TrialCohabitationDTO>>() {}.getType());
    }

    /**
     * GET /trial-cohabitations/{id}
     * Retrieves a specific trial cohabitation by its ID,
     * including nested pet and adoption information.
     *
     * @param id The ID of the trial cohabitation to retrieve.
     * @return TrialCohabitationDetailDTO with full details.
     * @throws EntityNotFoundException if no trial cohabitation with the given ID exists.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrialCohabitationDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        TrialCohabitationEntity entity = trialCohabitationService.getTrialCohabitation(id);
        return modelMapper.map(entity, TrialCohabitationDetailDTO.class);
    }

    /**
     * POST /trial-cohabitations
     * Creates a new trial cohabitation.
     *
     * Business rules:
     *   - startDate and endDate are required.
     *   - endDate must be after startDate.
     *   - result (if provided) must be one of: "EN_PROCESO", "EXITOSA", "FALLIDA", "CANCELADA".
     *
     * @param trialDTO DTO containing the trial cohabitation data to persist.
     * @return TrialCohabitationDTO of the newly created record.
     * @throws IllegalOperationException if any business rule is violated.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrialCohabitationDTO create(@RequestBody TrialCohabitationDTO trialDTO)
            throws IllegalOperationException {
        TrialCohabitationEntity entity = modelMapper.map(trialDTO, TrialCohabitationEntity.class);
        TrialCohabitationEntity created = trialCohabitationService.createTrialCohabitation(entity);
        return modelMapper.map(created, TrialCohabitationDTO.class);
    }

    /**
     * PUT /trial-cohabitations/{id}
     * Updates an existing trial cohabitation.
     *
     * Business rules:
     *   - result can only transition FROM "EN_PROCESO" to another valid state.
     *   - Dates remain validated (endDate must be after startDate).
     *
     * @param id       The ID of the trial cohabitation to update.
     * @param trialDTO DTO with the updated data.
     * @return TrialCohabitationDTO of the updated record.
     * @throws EntityNotFoundException   if no trial cohabitation with the given ID exists.
     * @throws IllegalOperationException if business rules are violated (e.g., invalid result transition).
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrialCohabitationDTO update(@PathVariable Long id, @RequestBody TrialCohabitationDTO trialDTO)
            throws EntityNotFoundException, IllegalOperationException {
        TrialCohabitationEntity entity = modelMapper.map(trialDTO, TrialCohabitationEntity.class);
        TrialCohabitationEntity updated = trialCohabitationService.updateTrialCohabitation(id, entity);
        return modelMapper.map(updated, TrialCohabitationDTO.class);
    }

    /**
     * DELETE /trial-cohabitations/{id}
     * Deletes a trial cohabitation by its ID.
     * Not allowed if the trial period is currently in progress (today is between startDate and endDate).
     *
     * @param id The ID of the trial cohabitation to delete.
     * @throws EntityNotFoundException   if no trial cohabitation with the given ID exists.
     * @throws IllegalOperationException if the trial is currently in progress.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        trialCohabitationService.deleteTrialCohabitation(id);
    }
}
