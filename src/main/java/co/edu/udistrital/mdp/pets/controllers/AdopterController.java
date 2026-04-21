package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.*;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdopterService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
/**
 * REST controller for managing Adopter resources.
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
     * Retorna todos los adoptantes registrados usando DetailDTO para cumplir la Rule 17.
     */
    @GetMapping
    public ResponseEntity<List<AdopterDetailDTO>> findAll() {
        return ResponseEntity.ok(modelMapper.map(
            adopterService.getUsers(), 
            new TypeToken<List<AdopterDetailDTO>>() {}.getType()
        ));
    }

    /**
     * GET /adopters/{id}
     * Retorna el detalle de un adoptante específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdopterDetailDTO> findOne(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            adopterService.getUser(id), 
            AdopterDetailDTO.class
        ));
    }

    /**
     * POST /adopters
     * Crea un adoptante delegando el mapeo de la entidad al service para evitar imports prohibidos.
     */
	@PostMapping
    public ResponseEntity<AdopterDTO> create(@RequestBody AdopterDTO adopterDTO) throws IllegalOperationException {
        // Cero entidades. El Service se encarga de todo.
        return ResponseEntity.status(HttpStatus.CREATED).body(adopterService.createFromDTO(adopterDTO));
    }

    /**
     * PUT /adopters/{id}
     * Actualiza los datos de un adoptante.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdopterDTO> update(@PathVariable Long id, @RequestBody AdopterDTO adopterDTO)
            throws EntityNotFoundException, IllegalOperationException {
        return ResponseEntity.ok(adopterService.updateFromDTO(id, adopterDTO));
    }

    /**
     * DELETE /adopters/{id}
     * Elimina un adoptante si no tiene restricciones de integridad.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        adopterService.deleteUser(id);
    }

    /**
     * GET /adopters/{id}/adoptions
     * Retorna las adopciones de un adoptante usando el método especializado del service.
     */
    @GetMapping("/{id}/adoptions")
    public ResponseEntity<List<AdoptionDetailDTO>> getAdoptions(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            adopterService.getAdoptionsByAdopter(id), 
            new TypeToken<List<AdoptionDetailDTO>>() {}.getType()
        ));
    }

    /**
     * GET /adopters/{id}/requests
     * Retorna las solicitudes de un adoptante usando el método especializado del service.
     */
    @GetMapping("/{id}/requests")
    public ResponseEntity<List<AdoptionRequestDetailDTO>> getAdoptionRequests(@PathVariable Long id) 
            throws EntityNotFoundException {
        return ResponseEntity.ok(modelMapper.map(
            adopterService.getRequestsByAdopter(id), 
            new TypeToken<List<AdoptionRequestDetailDTO>>() {}.getType()
        ));
    }
}
