package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AdopterRepository adopterRepository;

    @Mock
    private ShelterRepository shelterRepository;

    private PodamFactory factory = new PodamFactoryImpl();
    private MessageEntity messageEntity;
    private AdopterEntity adopterEntity;
    private ShelterEntity shelterEntity;

    @BeforeEach
    void setUp() {
        adopterEntity = factory.manufacturePojo(AdopterEntity.class);
        shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        messageEntity = factory.manufacturePojo(MessageEntity.class);
        
        messageEntity.setAdopter(adopterEntity);
        messageEntity.setShelter(shelterEntity);
    }

    // --- TESTS PARA createMessage ---

    @Test
    void createMessageSuccessTest() throws IllegalOperationException {
        when(adopterRepository.existsById(adopterEntity.getId())).thenReturn(true);
        when(shelterRepository.existsById(shelterEntity.getId())).thenReturn(true);
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(messageEntity);

        MessageEntity result = messageService.createMessage(messageEntity);

        assertNotNull(result);
        assertEquals(messageEntity.getContent(), result.getContent());
        assertFalse(result.getIsRead());
    }

    @Test
    void createMessageNullTest() {
        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(null));
    }

    @Test
    void createMessageEmptyContentTest() {
        messageEntity.setContent("");
        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(messageEntity));
    }

    @Test
    void createMessageNoAdopterTest() {
        messageEntity.setAdopter(null);
        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(messageEntity));
    }

    @Test
    void createMessageAdopterNotFoundTest() {
        when(adopterRepository.existsById(adopterEntity.getId())).thenReturn(false);
        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(messageEntity));
    }

    // --- TESTS PARA getMessage ---

    @Test
    void getMessageSuccessTest() throws EntityNotFoundException {
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        MessageEntity result = messageService.getMessage(messageEntity.getId());
        assertEquals(messageEntity.getId(), result.getId());
    }

    @Test
    void getMessageNotFoundTest() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessage(1L));
    }

    // --- TESTS PARA getMessagesByRole ---

    @Test
    void getMessagesByShelterSuccessTest() throws EntityNotFoundException {
        when(shelterRepository.existsById(shelterEntity.getId())).thenReturn(true);
        when(messageRepository.findByShelterId(shelterEntity.getId())).thenReturn(new ArrayList<>());
        
        List<MessageEntity> result = messageService.getMessagesByShelter(shelterEntity.getId());
        assertNotNull(result);
    }

    @Test
    void getMessagesByAdopterNotFoundTest() {
        when(adopterRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessagesByAdopter(1L));
    }

    // --- TESTS PARA updateMessage ---

    @Test
    void updateMessageSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        MessageEntity newData = new MessageEntity();
        newData.setContent("New Content Updated");

        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(messageEntity);

        MessageEntity result = messageService.updateMessage(messageEntity.getId(), newData);
        
        assertEquals("New Content Updated", result.getContent());
    }

    @Test
    void updateMessageEmptyContentTest() {
        MessageEntity newData = new MessageEntity();
        newData.setContent(" ");
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        
        assertThrows(IllegalOperationException.class, () -> messageService.updateMessage(messageEntity.getId(), newData));
    }

    // --- TESTS PARA deleteMessage ---

    @Test
    void deleteMessageAsAdopterSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        
        assertDoesNotThrow(() -> messageService.deleteMessage(messageEntity.getId(), adopterEntity.getId(), true));
        verify(messageRepository, times(1)).deleteById(messageEntity.getId());
    }

    @Test
    void deleteMessageAsShelterSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        
        assertDoesNotThrow(() -> messageService.deleteMessage(messageEntity.getId(), shelterEntity.getId(), false));
        verify(messageRepository, times(1)).deleteById(messageEntity.getId());
    }

    @Test
    void deleteMessageForbiddenTest() {
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        
        // Un usuario que no es ni el adopter ni el shelter del mensaje
        assertThrows(IllegalOperationException.class, () -> messageService.deleteMessage(messageEntity.getId(), 999L, true));
    }

    // --- TESTS PARA markAsRead ---

    @Test
    void markAsReadTest() throws EntityNotFoundException {
        when(messageRepository.findById(messageEntity.getId())).thenReturn(Optional.of(messageEntity));
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(messageEntity);

        MessageEntity result = messageService.markAsRead(messageEntity.getId());
        
        assertTrue(result.getIsRead());
    }
    
    @Test
    void getMessagesTest() {
        List<MessageEntity> list = new ArrayList<>();
        list.add(messageEntity);
        when(messageRepository.findAll()).thenReturn(list);
        
        List<MessageEntity> result = messageService.getMessages();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
