package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AdopterRepository adopterRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    /**
     * Valida que el mensaje tenga contenido y que tanto emisor como receptor existan.
     */
    private void validateMessage(MessageEntity message) throws IllegalOperationException {
        if (message == null)
            throw new IllegalOperationException("Message data cannot be null");

        if (message.getContent() == null || message.getContent().trim().isEmpty())
            throw new IllegalOperationException("Message content cannot be empty");

        // Validación de existencia de Adopter
        if (message.getAdopter() == null || message.getAdopter().getId() == null)
            throw new IllegalOperationException("Message must involve an adopter");

        // Validación de existencia de Shelter
        if (message.getShelter() == null || message.getShelter().getId() == null)
            throw new IllegalOperationException("Message must involve a shelter");

        if (!adopterRepository.existsById(message.getAdopter().getId()))
            throw new IllegalOperationException("Adopter does not exist");

        if (!shelterRepository.existsById(message.getShelter().getId()))
            throw new IllegalOperationException("Shelter does not exist");
    }

	@Transactional
	public MessageEntity createMessage(MessageEntity message) throws IllegalOperationException {
		// Primero validamos para evitar el NPE en los logs
		validateMessage(message);

		log.info("Creating message between adopter {} and shelter {}", 
				message.getAdopter().getId(), message.getShelter().getId());
		
		if (message.getIsRead() == null) {
			message.setIsRead(false);
		}

		return messageRepository.save(message);
	}

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessages() {
        return messageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MessageEntity getMessage(Long messageId) throws EntityNotFoundException {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessagesByShelter(Long shelterId) throws EntityNotFoundException {
        if (!shelterRepository.existsById(shelterId))
            throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);
        return messageRepository.findByShelterId(shelterId);
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessagesByAdopter(Long adopterId) throws EntityNotFoundException {
        if (!adopterRepository.existsById(adopterId))
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
        return messageRepository.findByAdopterId(adopterId);
    }

    @Transactional
    public MessageEntity updateMessage(Long messageId, MessageEntity messageData)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating message with id = {}", messageId);

        MessageEntity existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));

        // Solo permitimos actualizar el contenido para mantener integridad
        if (messageData.getContent() == null || messageData.getContent().trim().isEmpty())
            throw new IllegalOperationException("New content cannot be empty");

        existingMessage.setContent(messageData.getContent());
        
        return messageRepository.save(existingMessage);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long requestingUserId, boolean isAdopter)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting message with id = {}", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));

        // Lógica bidireccional: Verifica que quien borra sea parte de la conversación
        boolean isOwner;
        if (isAdopter) {
            isOwner = message.getAdopter().getId().equals(requestingUserId);
        } else {
            isOwner = message.getShelter().getId().equals(requestingUserId);
        }

        if (!isOwner) {
            throw new IllegalOperationException("Only the participants can delete this message");
        }

        messageRepository.deleteById(messageId);
    }

    @Transactional
	public MessageEntity markAsRead(Long messageId) throws EntityNotFoundException {
		// Usar el repositorio directamente evita el self-invocation
		MessageEntity message = messageRepository.findById(messageId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));
		
		message.setIsRead(true);
		return messageRepository.save(message);
}
}
