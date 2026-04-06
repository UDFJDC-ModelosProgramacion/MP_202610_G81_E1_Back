package co.edu.udistrital.mdp.pets.controllers;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.udistrital.mdp.pets.dto.ShelterEventDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterEventDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterEventService;
@RestController
@RequestMapping("/shelter-events")
public class ShelterEventController {
    @Autowired
    private ShelterEventService shelterEventService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterEventDetailDTO createShelterEvent(@RequestBody ShelterEventDetailDTO dto)
            throws IllegalOperationException {
        ShelterEventEntity entity = modelMapper.map(dto, ShelterEventEntity.class);
        return modelMapper.map(shelterEventService.createShelterEvent(entity), ShelterEventDetailDTO.class);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterEventDTO> getShelterEvents() {
        List<ShelterEventEntity> entities = shelterEventService.getShelterEvents();
        return modelMapper.map(entities, new TypeToken<List<ShelterEventDTO>>() {}.getType());
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterEventDetailDTO getShelterEvent(@PathVariable Long id)
            throws EntityNotFoundException {
        return modelMapper.map(shelterEventService.getShelterEvent(id), ShelterEventDetailDTO.class);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterEventDetailDTO updateShelterEvent(@PathVariable Long id,
            @RequestBody ShelterEventDetailDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEventEntity entity = modelMapper.map(dto, ShelterEventEntity.class);
        return modelMapper.map(shelterEventService.updateShelterEvent(id, entity), ShelterEventDetailDTO.class);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShelterEvent(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        shelterEventService.deleteShelterEvent(id);
    }
    @PatchMapping("/{id}/finish")
    @ResponseStatus(HttpStatus.OK)
    public ShelterEventDetailDTO finishEvent(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(shelterEventService.finishEvent(id), ShelterEventDetailDTO.class);
    }
}
