package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import co.edu.udistrital.mdp.pets.entities.ManualApprovalStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalClearanceStrategyEntity;
import co.edu.udistrital.mdp.pets.entities.ScoreBasedApprovalStrategyEntity;

/**
 * llenar :) 
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.EXISTING_PROPERTY, // Cambia a EXISTING_PROPERTY
  property = "type", 
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ManualApprovalStrategyEntity.class, name = "MANUAL"),
  @JsonSubTypes.Type(value = MedicalClearanceStrategyEntity.class, name = "MEDICAL"),
  @JsonSubTypes.Type(value = ScoreBasedApprovalStrategyEntity.class, name = "SCORE")
})
public class ApprovalStrategyDTO extends BaseDTO {
	@JsonProperty("type")
    public String getType() {
        return this.getClass().getSimpleName()
                   .replace("ApprovalStrategyEntity", "")
                   .replace("Entity", "")
                   .toUpperCase();
    }
}
