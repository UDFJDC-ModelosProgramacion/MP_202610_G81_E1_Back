package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.VaccineEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VaccineService.class)
class VaccineServiceTest {

    @Autowired private VaccineService vaccineService;
    @Autowired private TestEntityManager entityManager;
    private final PodamFactory factory = new PodamFactoryImpl();

    private VaccineEntity persistedVaccine;

    @BeforeEach
    public void setUp() {
        entityManager.getEntityManager().createQuery("delete from VaccineEntity").executeUpdate();
        VaccineEntity v = factory.manufacturePojo(VaccineEntity.class);
        v.setName(v.getName() == null ? "InitialVaccine" : v.getName());
        Integer vm = v.getValidityMonths();
        if (vm == null) {
            v.setValidityMonths(12);
        } else {
            v.setValidityMonths(vm);
        }
        persistedVaccine = entityManager.persistAndFlush(v);
    }

    // ==========================================
    // CREATION
    // ==========================================

    @Test
    void testCreateVaccineSuccess() throws IllegalOperationException {
        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        vaccine.setName("Rabies");
        vaccine.setValidityMonths(12);

        VaccineEntity result = vaccineService.createVaccine(vaccine);

        assertNotNull(result);
        assertNotNull(result.getId());

        assertNotNull(result.getValidityMonths(), "validityMonths must not be null");
        assertEquals(12, result.getValidityMonths().intValue());
    }

    @Test
    void testCreateVaccineEmptyNameFails() {
        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        vaccine.setName(""); 

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccineService.createVaccine(vaccine));
        assertNotNull(ex);
    }

    @Test
    void testCreateVaccineNullNameFails() {
        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        vaccine.setName(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccineService.createVaccine(vaccine));
        assertNotNull(ex);
    }

    @Test
    void testCreateVaccineValidityZeroFails() {
        VaccineEntity vaccine = factory.manufacturePojo(VaccineEntity.class);
        vaccine.setName("Test");
        vaccine.setValidityMonths(0);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccineService.createVaccine(vaccine));
        assertNotNull(ex);
    }

    // ==========================================
    // READ
    // ==========================================

    @Test
    void testGetVaccineSuccess() throws EntityNotFoundException {
        VaccineEntity found = vaccineService.getVaccine(persistedVaccine.getId());
        assertNotNull(found);
        assertEquals(persistedVaccine.getId(), found.getId());
    }

    @Test
    void testGetVaccineNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccineService.getVaccine(999999L));
        assertNotNull(ex);
    }

    // ==========================================
    // UPDATE
    // ==========================================

    @Test
    void testUpdateVaccineSuccess() throws EntityNotFoundException, IllegalOperationException {
        VaccineEntity update = factory.manufacturePojo(VaccineEntity.class);
        update.setName("UpdatedName");
        update.setValidityMonths(24);

        VaccineEntity updated = vaccineService.updateVaccine(persistedVaccine.getId(), update);

        assertNotNull(updated);
        assertEquals(persistedVaccine.getId(), updated.getId());
        assertEquals("UpdatedName", updated.getName());
        assertEquals(24, updated.getValidityMonths());
    }

    @Test
    void testUpdateVaccineNotFound() {
        VaccineEntity update = factory.manufacturePojo(VaccineEntity.class);
        update.setName("X");
        update.setValidityMonths(6);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccineService.updateVaccine(123456789L, update));
        assertNotNull(ex);
    }

    @Test
    void testUpdateVaccineInvalidNameFails() {
        VaccineEntity update = factory.manufacturePojo(VaccineEntity.class);
        update.setName(""); 
        update.setValidityMonths(6);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccineService.updateVaccine(persistedVaccine.getId(), update));
        assertNotNull(ex);
    }

    @Test
    void testUpdateVaccineInvalidValidityFails() {
        VaccineEntity update = factory.manufacturePojo(VaccineEntity.class);
        update.setName("Valid");
        update.setValidityMonths(0);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> vaccineService.updateVaccine(persistedVaccine.getId(), update));
        assertNotNull(ex);
    }

    // ==========================================
    // DELETE
    // ==========================================

    @Test
    void testDeleteVaccineSuccess() throws EntityNotFoundException {
        Long id = persistedVaccine.getId();
        vaccineService.deleteVaccine(id);
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccineService.getVaccine(id));
        assertNotNull(ex);
    }

    @Test
    void testDeleteVaccineNotFound() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> vaccineService.deleteVaccine(999999L));
        assertNotNull(ex);
    }
}
