package co.edu.udistrital.mdp.pets.entities;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;
import jakarta.persistence.*;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.ArrayList;
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ShelterEntity extends BaseEntity implements Subject{

    private String name;
    private String city;
    private String description;
    private String email;
    private String gallery;
	
	// --- Lógica del Patrón Observer ---
		
    @Transient // No se persiste, se maneja en tiempo de ejecución
    @PodamExclude
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message, NotificationStrategyEntity strategy) {
        for (Observer observer : observers) {
            NotificationEntity notification = new NotificationEntity();
            notification.setMessage(message);
            notification.setDate(new java.util.Date());
            notification.setIsRead(false);
            notification.setNotificationStrategy(strategy);
            
            // Cada UserEntity (Observer) procesará su propia notificación
            observer.update(notification);
        }
    }
	
	// Relation 1:N with Veterinarian (Agregattion)
    @PodamExclude
    @OneToMany(mappedBy = "shelter")
    @ToString.Exclude
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    // Relation 1:N with ShelterEvent
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<ShelterEventEntity> events = new ArrayList<>();

    // Relation 1:N with Report 
    @PodamExclude
    @OneToMany
	@JoinColumn(name = "shelter_id")
    private List<ReportEntity> reports = new ArrayList<>();

	// Relation 1:N with Message 
    @PodamExclude
    @OneToMany(mappedBy = "shelter")
    private List<MessageEntity> messages = new ArrayList<>();

	// Relation 1:N with Pet (Composition)
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PetEntity> pets = new ArrayList<>();

	// Relation 1:N with Suscription (Observer persistence)
	@PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionEntity> subscriptions = new ArrayList<>();

}
