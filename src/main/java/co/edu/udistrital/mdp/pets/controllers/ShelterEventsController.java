package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.ShelterEventDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelters/{shelterId}/events")
public class ShelterEventsController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Obtiene la agenda de eventos de un refugio.
     */
    @GetMapping
    public List<ShelterEventDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<ShelterEventEntity> events = shelterService.getShelter(shelterId).getEvents();
        return modelMapper.map(events, new TypeToken<List<ShelterEventDTO>>() {}.getType());
    }
}
