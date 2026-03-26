package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

/**
 * Base DTO for User information.
 * Abstract to prevent direct instantiation in the API.
 */
@Data
public abstract class UserDTO extends BaseDTO{
    private String name;
    private String email;
    private String phone;
    
    // CRITICAL: Notice there is NO password field here.
    // We never expose the password in a GET or PUT response.
}
