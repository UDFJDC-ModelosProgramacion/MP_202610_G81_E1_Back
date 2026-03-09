package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reviews")
@EqualsAndHashCode(callSuper = true)
public class ReviewEntity extends BaseEntity {

    @Column(name = "review_id")
    private Integer reviewId;

    @Column(nullable = false)
    private Integer rating;  // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable
