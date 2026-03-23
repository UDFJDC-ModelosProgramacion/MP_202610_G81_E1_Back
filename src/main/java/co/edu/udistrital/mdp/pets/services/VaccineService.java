package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.VaccineRepository;

@Service
public class VaccineService {

    @Autowired
    private VaccineRepository repository;

    /**
     * Valida las reglas de negocio de la vacuna.
     */
    private void validateVaccine(VaccineEntity vaccine) throws IllegalOperationException {
        // Validación de Nombre (Arregla los 3 fallos de Name en los tests)
        if (vaccine.getName() == null || vaccine.getName().trim().isEmpty()) {
            throw new IllegalOperationException("El nombre de la vacuna no puede estar vacío.");
        }
        
        // Validación de Validez (Arregla testCreateVaccineValidityZeroFails)
        if (vaccine.getValidityMonths() == null || vaccine.getValidityMonths() <= 0) {
            throw new IllegalOperationException("La validez debe ser mayor a 0 meses.");
        }
    }

    @Transactional
    public VaccineEntity createVaccine(VaccineEntity vaccine) throws IllegalOperationException {
        validateVaccine(vaccine);
        return repository.save(vaccine);
    }

    @Transactional(readOnly = true)
    public VaccineEntity getVaccine(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La vacuna no existe."));
    }

    @Transactional(readOnly = true)
    public List<VaccineEntity> getVaccines() {
        return repository.findAll();
    }

    @Transactional
    public VaccineEntity updateVaccine(Long id, VaccineEntity vaccine) throws EntityNotFoundException, IllegalOperationException {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("La vacuna no existe.");
        }
        
        validateVaccine(vaccine); // Validamos el nombre e id antes de actualizar
        vaccine.setId(id);
        return repository.save(vaccine);
    }

    @Transactional
    public void deleteVaccine(Long id) throws EntityNotFoundException {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("La vacuna no existe.");
        }
        repository.deleteById(id);
    }
}
