package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VaccineEntity extends BaseEntity {

    private String name;
    private String description;
    private Integer validityMonths; // Cuánto tiempo dura la vacuna

	@PodamExclude
    @OneToMany(mappedBy = "vaccine")
    private List<VaccinationRecordEntity> vaccinationRecords = new ArrayList<>();
}

