package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdoptionFollowUpEntity extends BaseEntity {

    private String frequency;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity veterinarian;

    @ManyToOne
    @JoinColumn(name = "adoption_id")
    private AdoptionEntity adoption;

    public void scheduleCheckUp() {
        // lógica para programar chequeo
    }

    public void recordObservation() {
        // lógica para registrar observación
    }
}
