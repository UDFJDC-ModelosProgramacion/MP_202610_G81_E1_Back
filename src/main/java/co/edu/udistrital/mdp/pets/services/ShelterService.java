package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.SubscriptionEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.enums.ProcessStatus;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShelterService {

	@Autowired
	private ShelterRepository shelterRepository;
	
	@Autowired
    private UserRepository userRepository; 

	// --- MÉTODOS DEL PATRÓN OBSERVER / SUBSCRIPTION ---

    /**
     * Suscribe un usuario a un refugio creando una SubscriptionEntity.
     */
    @Transactional
    public void subscribeUser(Long shelterId, Long userId) throws EntityNotFoundException {
        ShelterEntity shelter = shelterRepository.findById(shelterId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Verificar si ya existe la suscripción para no duplicar
        boolean alreadySubscribed = shelter.getSubscriptions().stream()
                .anyMatch(sub -> sub.getUser().getId().equals(userId));

        if (!alreadySubscribed) {
            SubscriptionEntity subscription = new SubscriptionEntity();
            subscription.setShelter(shelter);
            subscription.setUser(user);
            subscription.setActive(true);
            
            shelter.getSubscriptions().add(subscription);
            shelterRepository.save(shelter);
            log.info("User {} subscribed to shelter {}", userId, shelterId);
        }
    }

    /**
     * Notifica a todos los suscriptores activos usando el patrón Observer + Strategy.
     */
    @Transactional
    public void notifyAllSubscribers(Long shelterId, String message, NotificationStrategyEntity strategy) 
            throws EntityNotFoundException {
        
        ShelterEntity shelter = shelterRepository.findById(shelterId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND));

        shelter.getObservers().clear(); // Limpiar lista @Transient
        
        shelter.getSubscriptions().stream()
                .filter(SubscriptionEntity::getActive)
                .forEach(sub -> shelter.attach(sub.getUser()));

        log.info("Notifying {} observers in shelter {}", shelter.getObservers().size(), shelterId);
        shelter.notifyObservers(message, strategy);

        shelterRepository.save(shelter);
    }
	/**
	 * Crea un shelter en la persistencia.
	 *
	 * @param shelterEntity La entidad que representa el shelter a
	 *                           persistir.
	 * @return La entidad del shelter luego de persistirla.
	 * @throws BusinessLogicException Si la shelter a persistir ya existe.
	 */

	private void validateData(ShelterEntity shelter) throws IllegalOperationException {
		if (shelter.getName() == null || shelter.getName().isBlank()) 
            throw new IllegalOperationException("Name is mandatory");
        if (shelter.getEmail() == null || shelter.getEmail().isBlank()) 
            throw new IllegalOperationException("Email is mandatory");
        if (shelter.getCity() == null || shelter.getCity().isBlank()) 
            throw new IllegalOperationException("City is mandatory");
        if (shelter.getGallery() == null || shelter.getGallery().isBlank()) 
            throw new IllegalOperationException("Gallery/Photos are mandatory");
    }

	@Transactional
	public ShelterEntity createShelter(ShelterEntity shelterEntity)
			throws IllegalOperationException {
        if (shelterEntity == null) {
            throw new IllegalOperationException("Shelter data cannot be null");
        }
		log.info("Starting creation process for shelter: {}", shelterEntity.getName());

		validateData(shelterEntity);
	
		if (shelterRepository.existsByName(shelterEntity.getName())) {
            throw new IllegalOperationException("Shelter name already exists");
        }
        if (shelterRepository.existsByEmail(shelterEntity.getEmail())) {
            throw new IllegalOperationException("Shelter email already exists");
        }
					
		log.info("Shelter created successfully: {}", shelterEntity.getName());
		return shelterRepository.save(shelterEntity);
	}

	/**
	 * Obtener todas las shelteres existentes en la base de datos.
	 *
	 * @return una lista de shelteres.
	 */
	@Transactional(readOnly = true) 
	public List<ShelterEntity> getShelters() {
		log.info("Starting process to consult all shelters");
		return shelterRepository.findAll();
	}

	/**
	 * Obtener una shelter por medio de su id.
	 *
	 * @param shelterId: id de la shelter para ser buscada.
	 * @return la shelter solicitada por medio de su id.
	 */
	@Transactional(readOnly = true)
	public ShelterEntity getShelter(Long shelterId) throws EntityNotFoundException {
		log.info("Starting process to consult shelter with id = {}", shelterId);
		
		return shelterRepository.findById(shelterId)
				.orElseThrow(() -> {
					log.error("Shelter with id {} not found", shelterId);
					return new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);
				});
	}

	/**
	 * Buscar refugios por nombre parcial.
	 * * @param name El nombre parcial a buscar.
	 * @return Una lista de shelters que cumple con el criterio de busqueda.
	 */
	@Transactional(readOnly = true)
	public List<ShelterEntity> findSheltersByName(String name) {
		log.info("Searching shelters containing name: {}", name);
		return shelterRepository.findByNameContainingIgnoreCase(name);
	}

	/**
	 * Actualizar una shelter.
	 *
	 * @param shelterId: id de la shelter para buscarla en la base de
	 *                        datos.
	 * @param shelter:   shelter con los cambios para ser actualizada, por
	 *                        ejemplo el nombre.
	 * @return la shelter con los cambios actualizados en la base de datos.
	 */
	@Transactional
	public ShelterEntity updateShelter(Long shelterId, ShelterEntity shelter)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting update process for shelter with id = {}", shelterId);

		ShelterEntity existingShelter = shelterRepository.findById(shelterId)
        .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND));

		validateData(shelter);

		if (!shelter.getName().equals(existingShelter.getName()) && 
			shelterRepository.existsByName(shelter.getName())) {
			throw new IllegalOperationException("New shelter name already exists in another record");
		}

		if (!shelter.getEmail().equals(existingShelter.getEmail()) && 
			shelterRepository.existsByEmail(shelter.getEmail())) {
			throw new IllegalOperationException("New email already exists in another record");
		}
		shelter.setId(shelterId);
		log.info("Shelter with id = {} updated successfully", shelterId);
		return shelterRepository.save(shelter);
	}

	/**
	 * Borrar un shelter
	 *
	 * @param shelterId: id de la shelter a borrar
	 * @throws BusinessLogicException si la shelter tiene un premio asociado.
	 */
	@Transactional
    public void deleteShelter(Long shelterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting deletion process for shelter ID: {}", shelterId);
        
        ShelterEntity shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND));

        if (shelter.getPets() != null && !shelter.getPets().isEmpty()) {
            log.warn("Attempted to delete shelter {} but it has pets", shelterId);
            throw new IllegalOperationException("Cannot delete shelter: It has pets assigned.");
        }

        if (shelter.getEvents() != null) {
		boolean hasActiveEvents = shelter.getEvents().stream()
				.anyMatch(event -> event.getStatus() != ProcessStatus.COMPLETED);
			if (hasActiveEvents) {
			log.warn("Attempted to delete shelter {} but it has active events", shelterId);
			throw new IllegalOperationException("Cannot delete shelter: It has ongoing events.");
			}
		}
        shelterRepository.deleteById(shelterId);
        log.info("Shelter with ID: {} deleted successfully", shelterId);
    }

	@Transactional(readOnly = true)
	public boolean checkNameExists(String name) {
		log.info("Checking if shelter name exists: {}", name);
		return shelterRepository.existsByName(name);
	}

	@Transactional(readOnly = true)
	public boolean checkEmailExists(String email) {
		log.info("Checking if shelter email exists: {}", email);
		return shelterRepository.existsByEmail(email);
	}
}
