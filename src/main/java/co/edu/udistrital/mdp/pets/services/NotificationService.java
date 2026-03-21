package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.NotificationStrategyRepository;
import co.edu.udistrital.mdp.pets.repositories.UserRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationStrategyRepository notificationStrategyRepository;

    @Autowired
    private UserRepository userRepository;

    private void validateNotification(NotificationEntity notification) throws IllegalOperationException {
        if (notification == null)
            throw new IllegalOperationException("Notification data cannot be null");

        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty())
            throw new IllegalOperationException("Notification message cannot be empty");

        if (notification.getDate() == null)
            throw new IllegalOperationException("Notification date cannot be null");

        if (notification.getUser() == null || notification.getUser().getId() == null)
            throw new IllegalOperationException("Notification must have a recipient user");

        userRepository.findById(notification.getUser().getId())
                .orElseThrow(() -> new IllegalOperationException("Recipient user does not exist"));
    }

    @Transactional
    public NotificationEntity createNotification(NotificationEntity notification)
            throws IllegalOperationException {
        log.info("Creating notification for user: {}", 
                notification.getUser() != null ? notification.getUser().getId() : "null");

        validateNotification(notification);

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public NotificationEntity getNotification(Long notificationId) throws EntityNotFoundException {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsByUser(Long userId) throws EntityNotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        return notificationRepository.findByUserId(userId);
    }

    @Transactional
    public NotificationEntity updateNotification(Long notificationId, NotificationEntity notification)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating notification with id = {}", notificationId);

        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        validateNotification(notification);

        notification.setId(notificationId);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long notificationId) throws EntityNotFoundException {
        log.info("Deleting notification with id = {}", notificationId);

        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public NotificationEntity markAsRead(Long notificationId) throws EntityNotFoundException {
        log.info("Marking notification {} as read", notificationId);

        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public NotificationEntity setStrategy(Long notificationId, Long strategyId)
            throws EntityNotFoundException {
        log.info("Setting strategy {} to notification {}", strategyId, notificationId);

        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND));

        NotificationStrategyEntity strategy = notificationStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.NOTIFICATION_STRATEGY_NOT_FOUND));

        notification.setNotificationStrategy(strategy);
        return notificationRepository.save(notification);
    }
}
