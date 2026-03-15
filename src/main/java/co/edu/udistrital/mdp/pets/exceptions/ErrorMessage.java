package co.edu.udistrital.mdp.pets.exceptions;

/**
 * Utility class to centralize error messages used in services.
 */
public final class ErrorMessage {
    public static final String SHELTER_NOT_FOUND = "The shelter with the given id was not found";
    public static final String VETERINARIAN_NOT_FOUND = "The veterinarian with the given id was not found";
    public static final String PET_NOT_FOUND = "The pet with the given id was not found";
    public static final String USER_NOT_FOUND = "The user with the given id was not found";

    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
