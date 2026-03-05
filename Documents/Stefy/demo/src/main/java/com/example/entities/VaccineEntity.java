package com.example.entities;

import javax.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class VaccineEntity extends BaseEntity {
    private String name;
    private Integer recommendedInterval;
}