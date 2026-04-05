package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.MedicalHistoryDTO;
import co.edu.udistrital.mdp.pets.dto.MedicalHistoryDetailDTO;
import co.edu.udistrital.mdp.pets.dto.MedicalEventDTO;
import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MedicalHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/medical-histories")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService service;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<MedicalHistoryDetailDTO> create(@RequestBody MedicalHistoryDTO dto)
            throws IllegalOperationException {
        MedicalHistoryEntity toSave = modelMapper.map(dto, MedicalHistoryEntity.class);
        MedicalHistoryEntity created = service.createMedicalHistory(toSave);
        MedicalHistoryDetailDTO response = modelMapper.map(created, MedicalHistoryDetailDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MedicalHistoryDTO>> getAll() {
        List<MedicalHistoryDTO> list = service.getMedicalHistories().stream()
                .map(e -> modelMapper.map(e, MedicalHistoryDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalHistoryDetailDTO> getById(@PathVariable Long id)
            throws EntityNotFoundException {
        MedicalHistoryEntity entity = service.getMedicalHistory(id);
        MedicalHistoryDetailDTO dto = modelMapper.map(entity, MedicalHistoryDetailDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalHistoryDetailDTO> update(@PathVariable Long id, @RequestBody MedicalHistoryDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        MedicalHistoryEntity toUpdate = modelMapper.map(dto, MedicalHistoryEntity.class);
        MedicalHistoryEntity updated = service.updateMedicalHistory(id, toUpdate);
        MedicalHistoryDetailDTO response = modelMapper.map(updated, MedicalHistoryDetailDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.deleteMedicalHistory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<MedicalEventDTO>> getEvents(@PathVariable Long id) throws EntityNotFoundException {
        List<MedicalEventDTO> events = service.getMedicalHistory(id).getMedicalEvents().stream()
                .map(e -> modelMapper.map(e, MedicalEventDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
    }
}
