package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineEntity extends BaseEntity {

    private String name;
    private String description;
    private Integer validityMonths; // Cuánto tiempo dura la vacuna
}