package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.VaccineDTO;
import co.edu.udistrital.mdp.pets.dto.VaccineDetailDTO;
import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VaccineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vaccines")
@RequiredArgsConstructor
public class VaccineController {

    private final VaccineService service;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<VaccineDetailDTO> create(@RequestBody VaccineDTO dto) throws IllegalOperationException {
        VaccineEntity toSave = modelMapper.map(dto, VaccineEntity.class);
        VaccineEntity created = service.createVaccine(toSave);
        VaccineDetailDTO response = modelMapper.map(created, VaccineDetailDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VaccineDTO>> getAll() {
        List<VaccineDTO> list = service.getVaccines().stream()
                .map(e -> modelMapper.map(e, VaccineDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);    
	}

    @GetMapping("/{id}")
    public ResponseEntity<VaccineDetailDTO> getById(@PathVariable Long id) throws EntityNotFoundException {
        VaccineEntity entity = service.getVaccine(id);
        VaccineDetailDTO dto = modelMapper.map(entity, VaccineDetailDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaccineDetailDTO> update(@PathVariable Long id, @RequestBody VaccineDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        VaccineEntity toUpdate = modelMapper.map(dto, VaccineEntity.class);
        VaccineEntity updated = service.updateVaccine(id, toUpdate);
        VaccineDetailDTO response = modelMapper.map(updated, VaccineDetailDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.deleteVaccine(id);
        return ResponseEntity.noContent().build();
    }
}
