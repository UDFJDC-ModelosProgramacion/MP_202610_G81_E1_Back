package co.edu.udistrital.mdp.pets.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // use in inheritance
public class VeterinarianDetailDTO extends VeterinarianDTO {

    private List<VaccinationRecordDTO> vaccinationRecords = new ArrayList<>();
    private List<MedicalEventDTO> medicalEvents = new ArrayList<>();
    private List<AdoptionFollowUpDTO> adoptionFollowUps = new ArrayList<>();
}
