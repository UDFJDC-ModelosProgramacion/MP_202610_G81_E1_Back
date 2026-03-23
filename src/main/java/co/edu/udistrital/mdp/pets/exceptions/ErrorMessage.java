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
    public static final String MESSAGE_NOT_FOUND = "The message with the given id was not found";
    public static final String NOTIFICATION_NOT_FOUND = "The notification with the given id was not found";
    public static final String REVIEW_NOT_FOUND = "The review with the given id was not found";
    public static final String TRIAL_COHABITATION_NOT_FOUND = "The trial cohabitation with the given id was not found";
    public static final String NOTIFICATION_STRATEGY_NOT_FOUND = "The notification strategy with the given id was not found";
	public static final String SHELTER_EVENT_NOT_FOUND = "The shelter event with the given id was not found";
    public static final String MEDICAL_EVENT_NOT_FOUND = "Medical event not found";
    public static final String MEDICAL_HISTORY_NOT_FOUND = "Medical history not found";
    public static final String VACCINATION_NOT_FOUND = "Vaccination record not found";
    public static final String VACCINE_NOT_FOUND = "Vaccine not found";
    public static final String REPORT_NOT_FOUND = "The report does not exist.";
    public static final String REPORT_REASON_EMPTY = "The reason for the report cannot be empty.";
    public static final String REPORT_REPORTED_USER_REQUIRED = "The report must indicate the reported user.";
    public static final String REPORT_PERMISSION_DENIED = "You do not have permission to change the report status.";
    public static final String ADOPTION_FOLLOWUP_NOT_FOUND = "Adoption follow-up does not exist.";
    public static final String ADOPTION_NOT_COMPLETED = "Tracking cannot be created: adoption is not complete.";
    public static final String FOLLOWUP_DATE_REQUIRED = "The follow-up date is required.";
    public static final String FOLLOWUP_NOTES_REQUIRED = "The follow-up notes are required.";
    public static final String FOLLOWUP_PERMISSION_DENIED = "You do not have permission to modify this follow-up.";



    private ErrorMessage() {
        throw new IllegalStateException("Utility class");
    }
}
