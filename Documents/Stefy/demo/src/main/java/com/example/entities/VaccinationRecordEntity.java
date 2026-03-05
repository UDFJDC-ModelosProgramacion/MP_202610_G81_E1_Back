package com.example.entities;
import javax.persistence.*;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class VaccinationRecordEntity extends BaseEntity {
    private String applicationDate;

    @PodamExclude
    @ManyToOne
    private VaccineEntity vaccine;
}
