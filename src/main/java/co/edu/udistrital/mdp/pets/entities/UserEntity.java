package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    // Metodos del diagrama (Logica se implementa en Service)
    // public void login() {}
    // public void logout() {}
    
    /**
     * Metodo del patron Observer para notificaciones
     * @param notification Mensaje o evento a notificar
     */
    public abstract void update(String notification);
}
