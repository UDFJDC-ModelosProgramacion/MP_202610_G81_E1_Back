package co.edu.udistrital.mdp.pets.strategies;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import co.edu.udistrital.mdp.pets.entities.EmailNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.InAppNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.SMSNotificationStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationStrategyTest {

    private NotificationEntity notification;

	@BeforeEach
	void setUp() {
		notification = new NotificationEntity();
		notification.setMessage("Prueba de notificación");
		notification.setIsRead(false);
	}

    @Test
    void testEmailStrategyCoverage() {
        EmailNotificationStrategyEntity strategy = new EmailNotificationStrategyEntity();
        strategy.send(notification);
        assertNotNull(strategy, "La estrategia de Email debería instanciarse correctamente");
    }

    @Test
    void testSMSStrategyCoverage() {
        SMSNotificationStrategyEntity strategy = new SMSNotificationStrategyEntity();
        strategy.send(notification);
        assertNotNull(strategy, "La estrategia de SMS debería instanciarse correctamente");
    }

    @Test
    void testInAppStrategyCoverage() {
        InAppNotificationStrategyEntity strategy = new InAppNotificationStrategyEntity();
        strategy.send(notification);
        assertNotNull(strategy, "La estrategia In-App debería instanciarse correctamente");
    }
}
