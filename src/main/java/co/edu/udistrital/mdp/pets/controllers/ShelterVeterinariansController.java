package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelters/{shelterId}/veterinarians")
public class ShelterVeterinariansController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Obtiene todos los veterinarios asociados a un refugio específico.
     */
    @GetMapping
    public List<VeterinarianDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<VeterinarianEntity> veterinarians = shelterService.getShelter(shelterId).getVeterinarians();
        return modelMapper.map(veterinarians, new TypeToken<List<VeterinarianDTO>>() {}.getType());
    }
}
