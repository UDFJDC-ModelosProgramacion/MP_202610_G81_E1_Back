package co.edu.udistrital.mdp.pets.enums;

public enum PetStatus {
    AVAILABLE,    // Disponible para adopción
    IN_TRIAL,     // En periodo de convivencia (Trial Cohabitation)
    ADOPTED,      // Ya tiene un hogar permanente
    RESERVED,     // Alguien inicio la solicitud pero no ha empezado la prueba
    MEDICAL_TREATMENT // No disponible temporalmente por salud
}
