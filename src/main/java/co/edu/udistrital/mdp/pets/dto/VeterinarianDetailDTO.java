package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true) // use in inheritance
@NoArgsConstructor
public class VeterinarianDetailDTO extends VeterinarianDTO {
}
