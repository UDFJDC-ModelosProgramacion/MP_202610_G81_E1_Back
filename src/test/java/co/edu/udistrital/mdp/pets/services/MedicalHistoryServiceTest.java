package co.edu.udistrital.mdp.pets.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.MedicalHistoryEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@DataJpaTest
@Import(MedicalHistoryService.class)
class MedicalHistoryServiceTest {

    @Autowired private MedicalHistoryService service;
    @Autowired private TestEntityManager entityManager;
    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<MedicalHistoryEntity> data = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        entityManager.getEntityManager().createQuery("delete from MedicalHistoryEntity").executeUpdate();
        for (int i = 0; i < 3; i++) {
            MedicalHistoryEntity entity = factory.manufacturePojo(MedicalHistoryEntity.class);
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            entityManager.persist(pet);
            entity.setPet(pet);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test void testCreate() throws IllegalOperationException {
        MedicalHistoryEntity newE = factory.manufacturePojo(MedicalHistoryEntity.class);
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);
        newE.setPet(pet);
        assertNotNull(service.createMedicalHistory(newE));
    }

    @Test void testCreateFail() {
        MedicalHistoryEntity newE = factory.manufacturePojo(MedicalHistoryEntity.class);
        newE.setPet(null);
        Throwable thrown = assertThrows(IllegalOperationException.class, () -> service.createMedicalHistory(newE));
        assertNotNull(thrown);
    }

    @Test void testUpdate() throws EntityNotFoundException, IllegalOperationException {
        MedicalHistoryEntity entity = data.get(0);
        MedicalHistoryEntity pojo = factory.manufacturePojo(MedicalHistoryEntity.class);
        pojo.setPet(entity.getPet());
        assertNotNull(service.updateMedicalHistory(entity.getId(), pojo));
    }

    @Test void testDelete() throws EntityNotFoundException {
        service.deleteMedicalHistory(data.get(0).getId());
        assertNull(entityManager.find(MedicalHistoryEntity.class, data.get(0).getId()));
    }
}