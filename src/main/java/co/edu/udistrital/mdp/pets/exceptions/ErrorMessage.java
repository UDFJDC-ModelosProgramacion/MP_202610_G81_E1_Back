package co.edu.udistrital.mdp.pets.exceptions;

/**
 * Utility class to centralize error messages used in services.
 */
public final class ErrorMessage {
    public static final String SHELTER_NOT_FOUND = "The shelter with the given id was not found";
    public static final String VETERINARIAN_NOT_FOUND = "The veterinarian with the given id was not found";
    public static final String PET_NOT_FOUND = "The pet with the given id was not found";
    public static final String USER_NOT_FOUND = "The user with the given id was not found";
    public static final String ADOPTER_NOT_FOUND = "The adopter with the given id was not found";
    public static final String ADOPTION_NOT_FOUND = "The adoption with the given id was not found";
    public static final String TRIAL_COHABITATION_NOT_FOUND = "The trial cohabitation with the given id was not found";

    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
