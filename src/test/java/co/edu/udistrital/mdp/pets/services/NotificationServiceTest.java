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
@EntityScan("co.edu.udistrital.mdp.pets.entities")
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
        AdopterEntity adopter = new AdopterEntity();
        adopter.setName("Test");
        adopter.setEmail("test@udistrital.edu.co");
        adopter.setPassword("password123");
        adopter.setPhone("3001234567");
        entityManager.persist(adopter);
        this.user = adopter;

        for (int i = 0; i < 3; i++) {
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
        UserEntity userWithoutId = new AdopterEntity();
        userWithoutId.setId(null);
        newNotification.setUser(userWithoutId);
        newNotification.setMessage("Valid message");
        newNotification.setDate(new java.util.Date());

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationEmptyMessageTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("");

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationNullMessageTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setDate(new java.util.Date());
        newNotification.setMessage(null);

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationNullDateTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("Valid message");
        newNotification.setDate(null);

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationNullStrategyTest() {
        NotificationEntity newNotification = new NotificationEntity();
        newNotification.setUser(user);
        newNotification.setMessage("Valid message");
        newNotification.setDate(new java.util.Date());
        newNotification.setNotificationStrategy(null);

        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(newNotification));
    }

    @Test
    void createNotificationStrategyIdNullTest() {
        NotificationEntity newNotification = new NotificationEntity();
        NotificationStrategyEntity strategyWithoutId = new EmailNotificationStrategyEntity();
        strategyWithoutId.setId(null);
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
        newNotification.setDate(new java.util.Date());

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
        newNotification.setIsRead(null);

        NotificationEntity result = notificationService.createNotification(newNotification);
        assertNotNull(result);
        assertFalse(result.getIsRead());
    }

    @Test
    void getNotificationsTest() {
        List<NotificationEntity> results = notificationService.getNotifications();
        assertEquals(data.size(), results.size());
    }

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

    @Test
    void updateNotificationSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity entity = data.get(0);

        NotificationEntity updateData = new NotificationEntity();
        updateData.setUser(user);
        updateData.setMessage("Updated Message Content");
        updateData.setDate(new java.util.Date());
        updateData.setNotificationStrategy(entity.getNotificationStrategy());

        NotificationEntity result = notificationService.updateNotification(entity.getId(), updateData);
        assertEquals("Updated Message Content", result.getMessage());
    }

    @Test
    void updateNotificationNotFoundTest() {
        EmailNotificationStrategyEntity strategy = new EmailNotificationStrategyEntity();
        entityManager.persist(strategy);

        NotificationEntity updateData = new NotificationEntity();
        updateData.setUser(user);
        updateData.setMessage("Updated Message");
        updateData.setDate(new java.util.Date());
        updateData.setNotificationStrategy(strategy);

        assertThrows(EntityNotFoundException.class, () ->
            notificationService.updateNotification(999L, updateData));
    }

    @Test
    void updateNotificationNullStrategyTest() {
        NotificationEntity entity = data.get(0);

        NotificationEntity updateData = new NotificationEntity();
        updateData.setUser(user);
        updateData.setMessage("Updated Message Content");
        updateData.setDate(new java.util.Date());
        updateData.setNotificationStrategy(null);

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
    void markAsReadNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> notificationService.markAsRead(999L));
    }

    @Test
    void setStrategyTest() throws EntityNotFoundException {
        NotificationEntity entity = data.get(0);

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
