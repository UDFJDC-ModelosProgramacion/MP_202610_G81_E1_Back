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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.EmailNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(NotificationService.class)
@EntityScan("co.edu.udistrital.mdp.pets.entities") // Explicitly scan for entities
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<NotificationEntity> data = new ArrayList<>();
    private UserEntity user;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from NotificationEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from NotificationStrategyEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
    }

    private void insertData() {
        // 1. Crear el Adopter (clase concreta de UserEntity)
        AdopterEntity adopter = new AdopterEntity();
        adopter.setName("Test");
        adopter.setEmail("test@udistrital.edu.co");
        adopter.setPassword("password123");
        adopter.setPhone("3001234567");
        entityManager.persist(adopter);
        this.user = adopter;

        for (int i = 0; i < 3; i++) {
            // 2. Usar la entidad concreta real para evitar errores de persister
            EmailNotificationStrategyEntity strategy = new EmailNotificationStrategyEntity();
            entityManager.persist(strategy);

            NotificationEntity entity = new NotificationEntity();
            entity.setMessage("Mensaje de prueba " + i);
            entity.setDate(new java.util.Date());
            entity.setIsRead(false);
            entity.setUser(this.user);
            entity.setNotificationStrategy(strategy);
            
            entityManager.persist(entity);
            data.add(entity);
        }
        entityManager.flush();
    }

    // --- TESTS DE CREACIÓN ---

    @Test
    void createNotificationSuccessTest() throws IllegalOperationException {
        EmailNotificationStrategyEntity newStrategy = new EmailNotificationStrategyEntity();
        entityManager.persist(newStrategy);

        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setMessage("Contenido nuevo de éxito");
        newNotification.setDate(new java.util.Date());
        newNotification.setUser(user);
        newNotification.setNotificationStrategy(newStrategy);

        NotificationEntity result = notificationService.createNotification(newNotification);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Contenido nuevo de éxito", result.getMessage());
    }

    @Test
    void createNotificationNullTest() {
        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(null));
    }

    @Test
    void createNotificationNullUserTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setMessage("Mensaje sin usuario");
        newNotification.setUser(null);
        
        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationUserIdNullTest() {
        NotificationEntity newNotification = new NotificationEntity();
        UserEntity userWithoutId = new AdopterEntity(); // Using AdopterEntity as a concrete UserEntity
        userWithoutId.setId(null); // Case: User with null ID
        newNotification.setUser(userWithoutId);
        newNotification.setMessage("Valid message");
        newNotification.setDate(new java.util.Date());

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationEmptyMessageTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage(""); // Mensaje vacío
        
        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }
    @Test
    void createNotificationNullMessageTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setDate(new java.util.Date());
        newNotification.setMessage(null); // Case: Null message

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationNullDateTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("Valid message");
        newNotification.setDate(null); // Case: Null date

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationNullStrategyTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("Valid message");
        newNotification.setDate(new java.util.Date());
        newNotification.setNotificationStrategy(null); // Case: Null strategy

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationStrategyIdNullTest() {
        NotificationEntity newNotification = new NotificationEntity();
        NotificationStrategyEntity strategyWithoutId = new EmailNotificationStrategyEntity();
        strategyWithoutId.setId(null); // Case: Strategy with null ID
        newNotification.setNotificationStrategy(strategyWithoutId);
        newNotification.setUser(user);
        newNotification.setMessage("Valid message");
        newNotification.setDate(new java.util.Date());

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationInvalidStrategyTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("Mensaje con estrategia inexistente");

        // Creamos una instancia pero NO la persistimos
        EmailNotificationStrategyEntity fakeStrategy = new EmailNotificationStrategyEntity();
        fakeStrategy.setId(999L);
        newNotification.setNotificationStrategy(fakeStrategy);

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationIsReadNullTest() throws IllegalOperationException {
        EmailNotificationStrategyEntity newStrategy = new EmailNotificationStrategyEntity();
        entityManager.persist(newStrategy);

        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setMessage("Valid content");
        newNotification.setDate(new java.util.Date());
        newNotification.setUser(user);
        newNotification.setNotificationStrategy(newStrategy);
        newNotification.setIsRead(null); // Case: isRead is null

        NotificationEntity result = notificationService.createNotification(newNotification);
        assertNotNull(result);
        assertFalse(result.getIsRead()); // Should default to false

        NotificationEntity entity = entityManager.find(NotificationEntity.class, result.getId());
        assertFalse(entity.getIsRead());
    }

    // --- TESTS DE BÚSQUEDA ---

    @Test
    void getNotificationsByUserTest() throws EntityNotFoundException {
        List<NotificationEntity> result = notificationService.getNotificationsByUser(user.getId());
        assertEquals(data.size(), result.size());
    }
    @Test
    void getNotificationsByInvalidUserTest() {
        assertThrows(EntityNotFoundException.class, () -> notificationService.getNotificationsByUser(999L));
    }

    @Test
    void getNotificationNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> notificationService.getNotification(999L));
    }

    // --- TESTS DE ACTUALIZACIÓN ---

    @Test
    void updateNotificationSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity entity = data.get(0);
        
        NotificationEntity updateData = new NotificationEntity();
        updateData.setUser(user); 
        updateData.setMessage("Updated Message Content");
        updateData.setDate(new java.util.Date());
        updateData.setNotificationStrategy(entity.getNotificationStrategy()); // Set existing strategy

        NotificationEntity result = notificationService.updateNotification(entity.getId(), updateData);
        assertEquals("Updated Message Content", result.getMessage());
    }

    @Test
    void updateNotificationNullStrategyTest() {
        NotificationEntity entity = data.get(0);
        
        NotificationEntity updateData = new NotificationEntity();
        updateData.setUser(user); 
        updateData.setMessage("Updated Message Content");
        updateData.setDate(new java.util.Date());
        updateData.setNotificationStrategy(null); // Case: Null strategy in update data

        assertThrows(IllegalOperationException.class, () ->
            notificationService.updateNotification(entity.getId(), updateData));
    }

    @Test
    void markAsReadTest() throws EntityNotFoundException {
        NotificationEntity entity = data.get(0);
        NotificationEntity result = notificationService.markAsRead(entity.getId());
        assertTrue(result.getIsRead());
    }
    @Test
    void setStrategyTest() throws EntityNotFoundException {
        NotificationEntity entity = data.get(0);
        
        // Nueva estrategia persistida
        EmailNotificationStrategyEntity newStrategy = new EmailNotificationStrategyEntity();
        entityManager.persist(newStrategy);

        NotificationEntity result = notificationService.setStrategy(entity.getId(), newStrategy.getId());
        
        assertNotNull(result.getNotificationStrategy());
        assertEquals(newStrategy.getId(), result.getNotificationStrategy().getId());
    }

    @Test
    void setStrategyInvalidNotificationTest() {
        EmailNotificationStrategyEntity newStrategy = new EmailNotificationStrategyEntity();
        entityManager.persist(newStrategy);

        assertThrows(EntityNotFoundException.class, () ->
            notificationService.setStrategy(999L, newStrategy.getId()));
    }

    @Test
    void setStrategyNotFoundTest() {
        NotificationEntity entity = data.get(0);
        assertThrows(EntityNotFoundException.class, () -> notificationService.setStrategy(entity.getId(), 999L));
    }
    // --- TESTS DE BORRADO ---

    @Test
    void deleteNotificationTest() throws EntityNotFoundException {
        NotificationEntity entity = data.get(0);
        notificationService.deleteNotification(entity.getId());
        
        NotificationEntity deleted = entityManager.find(NotificationEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteNotificationNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> notificationService.deleteNotification(999L));
    }
}
