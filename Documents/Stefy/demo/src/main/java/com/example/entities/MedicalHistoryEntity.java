package com.example.entities;
import java.util.*;
import javax.persistence.*;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class MedicalHistoryEntity extends BaseEntity {
    private String bloodType;

    @PodamExclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinationRecordEntity> vaccinationRecords = new ArrayList<>();

    @PodamExclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalEventEntity> medicalEvents = new ArrayList<>();
}