package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
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

    private void validateMessage(MessageEntity message) throws IllegalOperationException {
        if (message == null)
            throw new IllegalOperationException("Message data cannot be null");

        if (message.getContent() == null || message.getContent().trim().isEmpty())
            throw new IllegalOperationException("Message content cannot be empty");

        if (message.getAdopter() == null || message.getAdopter().getId() == null)
            throw new IllegalOperationException("Message must have a sender (adopter)");

        if (message.getShelter() == null || message.getShelter().getId() == null)
            throw new IllegalOperationException("Message must have a recipient (shelter)");

        adopterRepository.findById(message.getAdopter().getId())
                .orElseThrow(() -> new IllegalOperationException("Sender adopter does not exist"));

        shelterRepository.findById(message.getShelter().getId())
                .orElseThrow(() -> new IllegalOperationException("Recipient shelter does not exist"));
    }

    @Transactional
    public MessageEntity createMessage(MessageEntity message) throws IllegalOperationException {
        log.info("Creating message from adopter {} to shelter {}",
                message.getAdopter() != null ? message.getAdopter().getId() : "null",
                message.getShelter() != null ? message.getShelter().getId() : "null");

        validateMessage(message);

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
        shelterRepository.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND));
        return messageRepository.findByShelterId(shelterId);
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessagesByAdopter(Long adopterId) throws EntityNotFoundException {
        adopterRepository.findById(adopterId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND));
        return messageRepository.findByAdopterId(adopterId);
    }

    @Transactional
	public MessageEntity updateMessage(Long messageId, MessageEntity messageData)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Updating message with id = {}", messageId);

		// 1. Corregir el repositorio: usar messageRepository
		MessageEntity existingMessage = messageRepository.findById(messageId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));

		// 2. Validar los nuevos datos
		validateMessage(messageData);

		// 3. Mantener consistencia
		existingMessage.setContent(messageData.getContent());
		// No solemos cambiar el remitente/destinatario en un update de mensaje
		
		return messageRepository.save(existingMessage);
	}

    @Transactional
    public void deleteMessage(Long messageId, Long requestingAdopterId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting message with id = {}", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));

        if (!message.getAdopter().getId().equals(requestingAdopterId)) {
            throw new IllegalOperationException("Only the sender can delete this message");
        }

        messageRepository.deleteById(messageId);
    }

    @Transactional
    public MessageEntity markAsRead(Long messageId) throws EntityNotFoundException {
        log.info("Marking message {} as read", messageId);

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND));

        message.setIsRead(true);
        return messageRepository.save(message);
    }
}
