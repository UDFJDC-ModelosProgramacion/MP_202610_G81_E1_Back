package co.edu.udistrital.mdp.pets.exceptions;

public final class ErrorMessage {    
    public static final String ADOPTER_NOT_FOUND = "The adopter with the given id was not found";
    public static final String ADOPTION_NOT_FOUND = "The adoption with the given id was not found";
    public static final String TRIAL_COHABITATION_NOT_FOUND = "The trial cohabitation with the given id was not found";

    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
