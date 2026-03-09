package com.example.entities;
import javax.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class MedicalEventEntity extends BaseEntity {
    private String description;
    private String date;
}