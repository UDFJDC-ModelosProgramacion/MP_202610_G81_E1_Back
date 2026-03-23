package co.edu.udistrital.mdp.pets.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(MessageService.class)
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<MessageEntity> data = new ArrayList<>();
    private AdopterEntity adopter;
    private ShelterEntity shelter;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from MessageEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        // Creamos un emisor y receptor base para los mensajes
        adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        for (int i = 0; i < 3; i++) {
            MessageEntity entity = factory.manufacturePojo(MessageEntity.class);
            entity.setAdopter(adopter);
            entity.setShelter(shelter);
            entity.setIsRead(false);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test
    void createMessageTest() throws IllegalOperationException {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        newEntity.setShelter(shelter);
        newEntity.setContent("Contenido de prueba");

        MessageEntity result = messageService.createMessage(newEntity);
        assertNotNull(result);

        MessageEntity entity = entityManager.find(MessageEntity.class, result.getId());
        assertEquals(newEntity.getContent(), entity.getContent());
    }

    @Test
    void createMessageInvalidAdopterTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        AdopterEntity fakeAdopter = new AdopterEntity();
        fakeAdopter.setId(999L); // ID que no existe
        newEntity.setAdopter(fakeAdopter);
        newEntity.setShelter(shelter);

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageNullTest() {
        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(null));
    }

    @Test
    void getMessagesTest() {
        List<MessageEntity> list = messageService.getMessages();
        assertEquals(data.size(), list.size());
    }

    @Test
    void getMessageTest() throws EntityNotFoundException {
        MessageEntity entity = data.get(0);
        MessageEntity result = messageService.getMessage(entity.getId());
        assertNotNull(result);
        assertEquals(entity.getContent(), result.getContent());
    }

    @Test
    void updateMessageTest() throws EntityNotFoundException, IllegalOperationException {
        MessageEntity entity = data.get(0);
        MessageEntity pojo = factory.manufacturePojo(MessageEntity.class);
        pojo.setContent("Nuevo Contenido");

        MessageEntity result = messageService.updateMessage(entity.getId(), pojo);
        assertNotNull(result);
        assertEquals("Nuevo Contenido", result.getContent());
    }

    @Test
    void deleteMessageTest() throws EntityNotFoundException, IllegalOperationException {
        MessageEntity entity = data.get(0);
        messageService.deleteMessage(entity.getId(), adopter.getId(), true);
        MessageEntity deleted = entityManager.find(MessageEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteMessageForbiddenTest() {
        MessageEntity entity = data.get(0);
        // Intentamos borrar con un ID de usuario que no pertenece al mensaje
        assertThrows(IllegalOperationException.class, () -> 
            messageService.deleteMessage(entity.getId(), 999L, true));
    }

	// --- TESTS DE VALIDACIÓN (Lógica de Negocio) ---

    @Test
    void createMessageEmptyContentTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        newEntity.setShelter(shelter);
        newEntity.setContent(""); // Caso: Contenido vacío

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageNullContentTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        newEntity.setShelter(shelter);
        newEntity.setContent(null); // Case: Null content

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageNullAdopterTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(null); // Caso: Sin adoptante
        newEntity.setShelter(shelter);

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageAdopterIdNullTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        AdopterEntity adopterWithoutId = factory.manufacturePojo(AdopterEntity.class);
        adopterWithoutId.setId(null); // Case: Adopter with null ID
        newEntity.setAdopter(adopterWithoutId);
        newEntity.setShelter(shelter);
        newEntity.setContent("Valid content");

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageNullShelterTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        newEntity.setShelter(null); // Case: Null shelter
        newEntity.setContent("Valid content");

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageShelterIdNullTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        ShelterEntity shelterWithoutId = factory.manufacturePojo(ShelterEntity.class);
        shelterWithoutId.setId(null); // Case: Shelter with null ID
        newEntity.setShelter(shelterWithoutId);
        newEntity.setContent("Valid content");

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageShelterNotFoundTest() {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        
        ShelterEntity fakeShelter = new ShelterEntity();
        fakeShelter.setId(999L); // ID inexistente
        newEntity.setShelter(fakeShelter);

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(newEntity));
    }

    @Test
    void createMessageIsReadNullTest() throws IllegalOperationException {
        MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
        newEntity.setAdopter(adopter);
        newEntity.setShelter(shelter);
        newEntity.setContent("Valid content");
        newEntity.setIsRead(null); // Case: isRead is null

        MessageEntity result = messageService.createMessage(newEntity);
        assertNotNull(result);
        assertFalse(result.getIsRead()); // Should default to false

        MessageEntity entity = entityManager.find(MessageEntity.class, result.getId());
        assertFalse(entity.getIsRead());
    }

    // --- TESTS DE BÚSQUEDA ---

    @Test
    void getMessagesByShelterTest() throws EntityNotFoundException {
        List<MessageEntity> list = messageService.getMessagesByShelter(shelter.getId());
        assertEquals(data.size(), list.size());
    }

    @Test
    void getMessagesByShelterNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessagesByShelter(999L));
    }

    @Test
    void getMessagesByAdopterTest() throws EntityNotFoundException {
        List<MessageEntity> list = messageService.getMessagesByAdopter(adopter.getId());
        assertEquals(data.size(), list.size());
    }

    @Test
    void getMessagesByAdopterNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessagesByAdopter(999L));
    }

    @Test
    void getMessageInvalidIdTest() {
        assertThrows(EntityNotFoundException.class, () -> messageService.getMessage(999L));
    }

    // --- TESTS DE ACTUALIZACIÓN Y ESTADO ---

    @Test
    void updateMessageInvalidContentTest() {
        MessageEntity entity = data.get(0);
        MessageEntity pojo = new MessageEntity();
        pojo.setContent("   "); // Caso: Solo espacios en blanco

        assertThrows(IllegalOperationException.class, () -> 
            messageService.updateMessage(entity.getId(), pojo));
    }

    @Test
    void updateMessageNullContentTest() {
        MessageEntity entity = data.get(0);
        MessageEntity pojo = new MessageEntity();
        pojo.setContent(null); // Case: Null content

        assertThrows(IllegalOperationException.class, () ->
            messageService.updateMessage(entity.getId(), pojo));
    }

    @Test
    void markAsReadTest() throws EntityNotFoundException {
        MessageEntity entity = data.get(0);
        MessageEntity result = messageService.markAsRead(entity.getId());
        
        assertTrue(result.getIsRead());
        MessageEntity updated = entityManager.find(MessageEntity.class, entity.getId());
        assertTrue(updated.getIsRead());
    }

    // --- TESTS DE BORRADO (Casos de Borde) ---

    @Test
    void deleteMessageAsShelterSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        MessageEntity entity = data.get(0);
        // Borrado exitoso por parte del Shelter
        messageService.deleteMessage(entity.getId(), shelter.getId(), false);
        
        MessageEntity deleted = entityManager.find(MessageEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteMessageNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> 
            messageService.deleteMessage(999L, adopter.getId(), true));
    }

    @Test
    void deleteMessageUnauthorizedAdopterTest() {
        MessageEntity entity = data.get(0);
        
        AdopterEntity stranger = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(stranger);
        
        // Un adoptante que no es el dueño del mensaje intenta borrarlo
        assertThrows(IllegalOperationException.class, () -> 
            messageService.deleteMessage(entity.getId(), stranger.getId(), true));
    }
}
