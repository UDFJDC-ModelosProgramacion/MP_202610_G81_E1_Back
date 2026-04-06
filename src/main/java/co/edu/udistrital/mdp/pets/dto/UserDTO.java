package co.edu.udistrital.mdp.pets.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

	private List<NotificationDTO> notifications;
}
