package co.edu.udistrital.mdp.pets.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;


import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.services.ShelterService;

import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;@RestController

@RequestMapping("/shelters/{shelterId}/pets")
public class ShelterPetsController {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<PetDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        // Obtenemos las mascotas directamente de la entidad Shelter
        List<PetEntity> pets = shelterService.getShelter(shelterId).getPets();
        
        // Al mapear a PetDTO, asegúrate de que PetDTO no tenga un ShelterDTO completo 
        // para evitar la repetición, o que solo tenga el ID.
        return modelMapper.map(pets, new TypeToken<List<PetDTO>>() {}.getType());
    }
}
