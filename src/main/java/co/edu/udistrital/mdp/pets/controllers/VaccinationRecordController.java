package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.VaccinationRecordDTO;
import co.edu.udistrital.mdp.pets.dto.VaccinationRecordDetailDTO;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VaccinationRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vaccination-records")
@RequiredArgsConstructor
public class VaccinationRecordController {

    private final VaccinationRecordService service;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<VaccinationRecordDetailDTO> create(@RequestBody VaccinationRecordDTO dto)
            throws IllegalOperationException {
        VaccinationRecordEntity toSave = modelMapper.map(dto, VaccinationRecordEntity.class);
        VaccinationRecordEntity created = service.createVaccinationRecord(toSave);
        VaccinationRecordDetailDTO response = modelMapper.map(created, VaccinationRecordDetailDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VaccinationRecordDTO>> getAll() {
        List<VaccinationRecordDTO> list = service.getVaccinationRecords().stream()
                .map(e -> modelMapper.map(e, VaccinationRecordDTO.class))
                .toList(); 

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaccinationRecordDetailDTO> getById(@PathVariable Long id)
            throws EntityNotFoundException {
        VaccinationRecordEntity entity = service.getVaccinationRecord(id);
        VaccinationRecordDetailDTO dto = modelMapper.map(entity, VaccinationRecordDetailDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaccinationRecordDetailDTO> update(@PathVariable Long id, @RequestBody VaccinationRecordDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        VaccinationRecordEntity toUpdate = modelMapper.map(dto, VaccinationRecordEntity.class);
        VaccinationRecordEntity updated = service.updateVaccinationRecord(id, toUpdate);
        VaccinationRecordDetailDTO response = modelMapper.map(updated, VaccinationRecordDetailDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws EntityNotFoundException {
        service.deleteVaccinationRecord(id);
        return ResponseEntity.noContent().build();
    }
}
