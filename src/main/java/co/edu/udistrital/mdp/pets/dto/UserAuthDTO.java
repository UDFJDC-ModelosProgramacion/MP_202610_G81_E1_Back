package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

/**
 * UserAuthDTO se utiliza exclusivamente para procesos de "entrada" de datos 
 * sensibles, como el Registro o el Login. 
 * * Por que no usamos UserDTO? 
 * Porque UserDTO está diseñado para "mostrar" información (GET) y por seguridad 
 * NO tiene el campo 'password'. Si usaramos UserDTO para registrar a alguien, 
 * no tendríamos dónde recibir su contraseña.
 * * Con esta separación, garantizamos que la contraseña solo viaje desde el cliente 
 * hacia el servidor, pero nunca se incluya por error en las respuestas de la API.
 */
@Data
public class UserAuthDTO extends UserDTO {
    private String password;
}
