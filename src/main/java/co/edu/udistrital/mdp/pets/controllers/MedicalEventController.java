package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.MedicalEventDTO;
import co.edu.udistrital.mdp.pets.dto.MedicalEventDetailDTO;
import co.edu.udistrital.mdp.pets.entities.MedicalEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MedicalEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/medical-events")
@RequiredArgsConstructor
public class MedicalEventController {

    private final MedicalEventService service;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<MedicalEventDetailDTO> create(@RequestBody MedicalEventDTO dto)
            throws IllegalOperationException {
        MedicalEventEntity toSave = modelMapper.map(dto, MedicalEventEntity.class);
        MedicalEventEntity created = service.createMedicalEvent(toSave);
        MedicalEventDetailDTO response = modelMapper.map(created, MedicalEventDetailDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MedicalEventDTO>> getAll() {
        List<MedicalEventDTO> list = service.getMedicalEvents().stream()
                .map(e -> modelMapper.map(e, MedicalEventDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalEventDetailDTO> getById(@PathVariable Long id)
            throws EntityNotFoundException {
        MedicalEventEntity entity = service.getMedicalEvent(id);
        MedicalEventDetailDTO dto = modelMapper.map(entity, MedicalEventDetailDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalEventDetailDTO> update(@PathVariable Long id, @RequestBody MedicalEventDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        MedicalEventEntity toUpdate = modelMapper.map(dto, MedicalEventEntity.class);
        MedicalEventEntity updated = service.updateMedicalEvent(id, toUpdate);
        MedicalEventDetailDTO response = modelMapper.map(updated, MedicalEventDetailDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.deleteMedicalEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-history/{historyId}")
    public ResponseEntity<List<MedicalEventDTO>> getByHistory(@PathVariable Long historyId)
            throws EntityNotFoundException {
        List<MedicalEventDTO> list = service.getEventsByHistoryId(historyId).stream()
                .map(e -> modelMapper.map(e, MedicalEventDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
