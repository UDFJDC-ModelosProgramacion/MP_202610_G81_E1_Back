// No hay atributos, es solo para que la copilacion sea correcta!!!
package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class AdopterEntity extends BaseEntity {

    @PodamExclude
    @OneToMany(mappedBy = "adopter")
    private List<MessageEntity> messages = new ArrayList<>();
}
