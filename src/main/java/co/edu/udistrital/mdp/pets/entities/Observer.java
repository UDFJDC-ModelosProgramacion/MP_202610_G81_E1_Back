package co.edu.udistrital.mdp.pets.entities;

/**
 * Interface del patrón Observer.
 * Define el contrato para los objetos que deben ser notificados.
 */
public interface Observer {
    void update(NotificationEntity notification);
}
