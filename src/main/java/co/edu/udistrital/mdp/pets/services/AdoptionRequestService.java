package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequestEntity;
import co.edu.udistrital.mdp.pets.enums.PetStatus;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequestRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class AdoptionRequestService {

    @Autowired
    private AdoptionRequestRepository requestRepository;

    @Autowired
    private PetRepository petRepository;

    /**
     * Valida reglas de negocio para la creación de una solicitud.
     */
    private void validateNewRequest(AdoptionRequestEntity request) throws IllegalOperationException {
        // Regla 1: La mascota debe existir y estar AVAILABLE
        if (request.getPet() == null || request.getPet().getId() == null) {
            throw new IllegalOperationException("A pet must be associated with the request.");
        }
        
        // Recargamos de la DB para asegurar el estado actual
        var pet = petRepository.findById(request.getPet().getId())
                .orElseThrow(() -> new IllegalOperationException("The associated pet does not exist."));

        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalOperationException("The pet is not available for adoption. Current status: " + pet.getStatus());
        }

        // Regla 2: Un adoptante no puede tener múltiples solicitudes PENDING para la misma mascota
        if (request.getAdopter() != null && request.getAdopter().getId() != null) {
            List<AdoptionRequestEntity> existingRequests = requestRepository.findByAdopterIdAndPetId(
                    request.getAdopter().getId(), 
                    pet.getId()
            );
            
            boolean hasActiveRequest = existingRequests.stream()
                    .anyMatch(r -> r.getStatus().equalsIgnoreCase("PENDING"));

            if (hasActiveRequest) {
                throw new IllegalOperationException("You already have a pending request for this pet.");
            }
        }
    }

    /**
     * Valida la transición de estados de la solicitud (Máquina de estados).
     */
    private void validateStatusUpdate(AdoptionRequestEntity existing, String nextStatus) throws IllegalOperationException {
        String current = existing.getStatus();
        
        // Si ya está finalizada (APPROVED/REJECTED), no se puede mover
        if (current.equalsIgnoreCase("APPROVED") || current.equalsIgnoreCase("REJECTED")) {
            throw new IllegalOperationException("Cannot modify a request that is already " + current);
        }

        // Solo permitir cambios lógicos a APPROVED o REJECTED
        if (!nextStatus.equalsIgnoreCase("APPROVED") && !nextStatus.equalsIgnoreCase("REJECTED")) {
            throw new IllegalOperationException("Invalid status transition. Can only change to APPROVED or REJECTED.");
        }
    }

    @Transactional
    public AdoptionRequestEntity createRequest(AdoptionRequestEntity request) throws IllegalOperationException {
        log.info("Creating adoption request for pet: {}", request.getPet().getId());
        
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING"); // Siempre inicia en Pendiente
        
        validateNewRequest(request);
        
        return requestRepository.save(request);
    }

    @Transactional
    public AdoptionRequestEntity updateRequestStatus(Long requestId, String newStatus) 
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating status for request ID: {} to {}", requestId, newStatus);

        AdoptionRequestEntity existing = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Adoption Request not found"));

        validateStatusUpdate(existing, newStatus);
        
        existing.setStatus(newStatus.toUpperCase());
        return requestRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<AdoptionRequestEntity> getRequests() {
        return requestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AdoptionRequestEntity getRequest(Long id) throws EntityNotFoundException {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adoption Request not found"));
    }
}
