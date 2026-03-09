package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class MessageEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private Boolean isRead = false;

    // Relationship: Many messages belong to one Adopter
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private AdopterEntity adopter;

    // Relationship: Many messages belong to one Shelter
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;

    public void markAsRead() {
        this.isRead = true;
    }
}
