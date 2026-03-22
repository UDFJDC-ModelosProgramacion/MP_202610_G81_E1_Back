package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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

    private PodamFactory factory = new PodamFactoryImpl();

    private List<MessageEntity> data = new ArrayList<>();
    private AdopterEntity adopter;
    private ShelterEntity shelter;

    @BeforeEach
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
}
