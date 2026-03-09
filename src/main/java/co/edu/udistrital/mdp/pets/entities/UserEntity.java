package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.List;
import java.util.ArrayList;

@Data
@Entity

 // Cuando compare dos objetos, tambien revisa los campos que hereda del padre
@EqualsAndHashCode(callSuper = true)

// Estrategia para tablas unidas
@Inheritance(strategy = InheritanceType.JOINED) 
public abstract class UserEntity extends BaseEntity {

    private String name;
    private String email;
    private String phone;

	// Password es necesario para que el login() de los requerimientos funcione
    private String password;

    public void login() {
		// logic of login
	}
    public void logout() {
		// logic of logout
	}

	
	// Relation 1:N with Notification
	@PodamExclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications = new ArrayList<>();
	
    /**
     * Metodo del patron Observer para notificaciones
     * @param notification Mensaje o evento a notificar
     */
    public abstract void update(String notification);
}
