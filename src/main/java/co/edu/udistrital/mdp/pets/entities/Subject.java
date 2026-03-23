package co.edu.udistrital.mdp.pets.entities;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message, NotificationStrategyEntity strategy);
}
