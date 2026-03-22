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

import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Import(VaccineService.class)
class VaccineServiceTest {

    @Autowired private VaccineService service;
    @Autowired private TestEntityManager entityManager;
    private final PodamFactory factory = new PodamFactoryImpl();
    private final List<VaccineEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("delete from VaccineEntity").executeUpdate();
        for (int i = 0; i < 3; i++) {
            VaccineEntity entity = factory.manufacturePojo(VaccineEntity.class);
            entity.setValidityMonths(12);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test void testCreate() throws IllegalOperationException {
        VaccineEntity newE = factory.manufacturePojo(VaccineEntity.class);
        newE.setValidityMonths(24);
        assertNotNull(service.createVaccine(newE));
    }

    @Test void testCreateFailMonths() {
        VaccineEntity newE = factory.manufacturePojo(VaccineEntity.class);
        newE.setValidityMonths(0);
        Throwable exception = assertThrows(IllegalOperationException.class, () -> service.createVaccine(newE));
        assertNotNull(exception);
    }

    @Test void testUpdate() throws EntityNotFoundException, IllegalOperationException {
        VaccineEntity entity = data.get(0);
        VaccineEntity pojo = factory.manufacturePojo(VaccineEntity.class);
        pojo.setValidityMonths(48);
        assertNotNull(service.updateVaccine(entity.getId(), pojo));
    }

    @Test void testDelete() throws EntityNotFoundException {
        service.deleteVaccine(data.get(0).getId());
        assertNull(entityManager.find(VaccineEntity.class, data.get(0).getId()));
    }
}
