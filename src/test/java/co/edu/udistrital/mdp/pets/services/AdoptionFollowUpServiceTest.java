package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionFollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionFollowUpService.class)
@SuppressWarnings("null")
class AdoptionFollowUpServiceTest {

    @Autowired
    private AdoptionFollowUpService service;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        entityManager.getEntityManager().createQuery("delete from AdoptionFollowUpEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
        entityManager.clear();
    }

    @Test
    void testCreateFollowUpSuccess() throws IllegalOperationException {
        AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setPet(pet);
        adoption.setAdopter(adopter);
        adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED);
        entityManager.persist(adoption);

        VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
        entityManager.persist(vet);

        AdoptionFollowUpEntity followUp = new AdoptionFollowUpEntity();
        followUp.setAdoption(adoption);
        followUp.setPet(pet);
        followUp.setVeterinarian(vet);
        followUp.setFrequency("Mensual");
        followUp.setFollowUpDate(LocalDate.now());
        followUp.setNotes("Visita inicial OK");

        AdoptionFollowUpEntity saved = service.createFollowUp(followUp);

        assertNotNull(saved.getId());
        assertEquals("Mensual", saved.getFrequency());
        assertEquals("Visita inicial OK", saved.getNotes());
        assertEquals(adoption.getId(), saved.getAdoption().getId());
    }

    @Test
    void testCreateFollowUpWithoutAdoptionFails() {
        AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        followUp.setAdoption(null);
        followUp.setFollowUpDate(LocalDate.now());
        followUp.setNotes("Notas");

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createFollowUp(followUp));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.ADOPTION_NOT_COMPLETED));
    }

    @Test
    void testCreateFollowUpWhenAdoptionNotCompletedFails() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setPet(pet);
        adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.PENDING);
        entityManager.persist(adoption);

        AdoptionFollowUpEntity followUp = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        followUp.setAdoption(adoption);
        followUp.setFollowUpDate(LocalDate.now());
        followUp.setNotes("Notas");

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.createFollowUp(followUp));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.ADOPTION_NOT_COMPLETED));
    }

    @Test
    void testCreateFollowUpMissingDateOrNotesFails() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setPet(pet);
        adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED);
        entityManager.persist(adoption);

        AdoptionFollowUpEntity followUpNoDate = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        followUpNoDate.setAdoption(adoption);
        followUpNoDate.setFollowUpDate(null);
        followUpNoDate.setNotes("Notas");

        IllegalOperationException ex1 = assertThrows(IllegalOperationException.class,
                () -> service.createFollowUp(followUpNoDate));
        assertNotNull(ex1);
        assertTrue(ex1.getMessage().contains(ErrorMessage.FOLLOWUP_DATE_REQUIRED));

        AdoptionFollowUpEntity followUpNoNotes = factory.manufacturePojo(AdoptionFollowUpEntity.class);
        followUpNoNotes.setAdoption(adoption);
        followUpNoNotes.setFollowUpDate(LocalDate.now());
        followUpNoNotes.setNotes("   ");

        IllegalOperationException ex2 = assertThrows(IllegalOperationException.class,
                () -> service.createFollowUp(followUpNoNotes));
        assertNotNull(ex2);
        assertTrue(ex2.getMessage().contains(ErrorMessage.FOLLOWUP_NOTES_REQUIRED));
    }

    @Test
    void testUpdateFollowUpByShelterSuccess() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setPet(pet);
        adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED);
        entityManager.persist(adoption);

        AdoptionFollowUpEntity followUp = new AdoptionFollowUpEntity();
        followUp.setAdoption(adoption);
        followUp.setPet(pet);
        followUp.setFrequency("Semanal");
        followUp.setFollowUpDate(LocalDate.now());
        followUp.setNotes("Inicial");
        entityManager.persist(followUp);
        entityManager.flush();

        AdoptionFollowUpEntity updateData = new AdoptionFollowUpEntity();
        updateData.setFollowUpDate(LocalDate.now().plusDays(7));
        updateData.setNotes("Observación actualizada");
        updateData.setFrequency("Quincenal");

        AdoptionFollowUpEntity updated = service.updateFollowUp(followUp.getId(), updateData, true);
        assertEquals("Observación actualizada", updated.getNotes());
        assertEquals("Quincenal", updated.getFrequency());
    }

    @Test
    void testUpdateFollowUpByNonShelterFails() {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setPet(pet);
        adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED);
        entityManager.persist(adoption);

        AdoptionFollowUpEntity followUp = new AdoptionFollowUpEntity();
        followUp.setAdoption(adoption);
        followUp.setPet(pet);
        followUp.setFrequency("Semanal");
        followUp.setFollowUpDate(LocalDate.now());
        followUp.setNotes("Inicial");
        entityManager.persist(followUp);
        entityManager.flush();

        AdoptionFollowUpEntity updateData = new AdoptionFollowUpEntity();
        updateData.setFollowUpDate(LocalDate.now().plusDays(7));
        updateData.setNotes("Observación actualizada");

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> service.updateFollowUp(followUp.getId(), updateData, false));
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains(ErrorMessage.FOLLOWUP_PERMISSION_DENIED));
    }

    @Test
	void testFindByAdoptionIdAndVeterinarianAndFrequency() { 
		PetEntity pet = factory.manufacturePojo(PetEntity.class);
		entityManager.persist(pet);

		AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
		adoption.setPet(pet);
		adoption.setStatus(co.edu.udistrital.mdp.pets.enums.ProcessStatus.COMPLETED);
		entityManager.persist(adoption);

		VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
        entityManager.persist(vet);

        AdoptionFollowUpEntity f1 = new AdoptionFollowUpEntity();
        f1.setAdoption(adoption);
        f1.setPet(pet);
        f1.setVeterinarian(vet);
        f1.setFrequency("Mensual");
        f1.setFollowUpDate(LocalDate.now());
        f1.setNotes("A");
        entityManager.persist(f1);

        AdoptionFollowUpEntity f2 = new AdoptionFollowUpEntity();
        f2.setAdoption(adoption);
        f2.setPet(pet);
        f2.setVeterinarian(vet);
        f2.setFrequency("Semanal");
        f2.setFollowUpDate(LocalDate.now());
        f2.setNotes("B");
        entityManager.persist(f2);

        entityManager.flush();

        List<AdoptionFollowUpEntity> byAdoption = service.findByAdoptionId(adoption.getId());
        assertNotNull(byAdoption);
        assertTrue(byAdoption.size() >= 2);

        List<AdoptionFollowUpEntity> byVet = service.findByVeterinarianId(vet.getId());
        assertNotNull(byVet);
        assertTrue(byVet.size() >= 2);

        List<AdoptionFollowUpEntity> byFreq = service.findByFrequency("Mensual");
        assertNotNull(byFreq);
        assertEquals(1, byFreq.size());
        assertEquals("A", byFreq.get(0).getNotes());
    }
}
