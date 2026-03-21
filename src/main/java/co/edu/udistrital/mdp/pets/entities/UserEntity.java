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

//Se opto por la estrategia @Inheritance(strategy = JOINED) sobre @MappedSuperclass 
//para permitir asociaciones polimorficas. Dado que el sistema de notificaciones debe interactuar 
//con cualquier tipo de usuario de forma generica (Patrón Observer), 
//necesitamos que UserEntity sea una entidad real en el modelo de persistencia. 
//Usar @MappedSuperclass nos obligaría a duplicar la lógica de notificaciones en cada clase hija, 
//aumentando la deuda técnica y el acoplamiento.

// Estrategia para tablas unidas
@Inheritance(strategy = InheritanceType.JOINED) 
public abstract class UserEntity extends BaseEntity {

    private String name;
    private String email;
    private String phone;

	// Password es necesario para que el login() de los requerimientos funcione
    private String password;


	// Relation 1:N with Notification
	@PodamExclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications = new ArrayList<>();
}
