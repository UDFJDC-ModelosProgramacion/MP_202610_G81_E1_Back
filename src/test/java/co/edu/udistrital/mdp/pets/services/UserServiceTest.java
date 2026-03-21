package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.UserEntity;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Clase concreta Mock para poder testear la clase abstracta UserEntity.
 * @Entity es necesario para que JPA pueda persistirla en H2 durante el test.
 */

@DataJpaTest
@Transactional
@EntityScan(basePackages = {
    "co.edu.udistrital.mdp.pets.entities",
    "co.edu.udistrital.mdp.pets.services"
})
@Import(MockUserService.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<UserEntity> data = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            UserEntity entity = factory.manufacturePojo(MockUser.class);
            entity.setEmail("user" + i + "@test.com");
            entity.setPhone("300123456" + i);
            entityManager.persist(entity);
            data.add(entity);
        }
    }

    @Test
    void testCreateUser() throws IllegalOperationException {
        UserEntity newEntity = factory.manufacturePojo(MockUser.class);
        newEntity.setEmail("nuevo@correo.com");
        newEntity.setPhone("123456789");
        newEntity.setName("Yeremy Vega");
        newEntity.setPassword("admin123");

        UserEntity result = userService.createUser(newEntity);

        assertNotNull(result);
        UserEntity entity = entityManager.find(MockUser.class, result.getId());
        assertEquals(newEntity.getEmail(), entity.getEmail());
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setEmail(data.get(0).getEmail()); // Email ya existente
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setEmail("correo-sin-arroba.com");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithInvalidPhone() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setPhone("TEL-123-ABC"); // Contiene letras
            userService.createUser(newEntity);
        });
    }

    @Test
    void testGetUsers() {
        List<UserEntity> list = userService.getUsers();
        assertEquals(data.size(), list.size());
    }

    @Test
    void testGetUser() throws EntityNotFoundException {
        UserEntity entity = data.get(0);
        UserEntity result = userService.getUser(entity.getId());
        assertNotNull(result);
        assertEquals(entity.getEmail(), result.getEmail());
    }

    @Test
    void testUpdateUser() throws EntityNotFoundException, IllegalOperationException {
        UserEntity entity = data.get(0);
        UserEntity pojo = factory.manufacturePojo(MockUser.class);
        pojo.setEmail("cambio@test.com");
        pojo.setPhone("987654321");
        pojo.setName("Nombre Actualizado");
        pojo.setPassword("newpass123");

        UserEntity resp = userService.updateUser(entity.getId(), pojo);

        assertNotNull(resp);
        assertEquals(pojo.getEmail(), resp.getEmail());
        assertEquals(pojo.getPhone(), resp.getPhone());
    }

	@Test
	void testUpdateUserWithDuplicateEmail() {
		// Intentamos actualizar al usuario 1 con el email del usuario 2
		UserEntity user1 = data.get(0);
		UserEntity user2 = data.get(1);

		UserEntity pojo = factory.manufacturePojo(MockUser.class);
		pojo.setEmail(user2.getEmail()); // Email ya tomado por otro
		pojo.setPhone("321654987");
		pojo.setName("Cualquier Nombre");
		pojo.setPassword("pass123");

		assertThrows(IllegalOperationException.class, () -> {
			userService.updateUser(user1.getId(), pojo);
    });
	}

	@Test
	void testDeleteUserSuccess() throws EntityNotFoundException, IllegalOperationException {
		UserEntity entity = data.get(0);
		userService.deleteUser(entity.getId());
		
		UserEntity deleted = entityManager.find(MockUser.class, entity.getId());
		assertNull(deleted);
	}
	@Test
	void testDeleteUserWithActiveProcesses() {
		// fabricamos un AdopterEntity real
		// (Podam se encarga de llenar los campos de UserEntity que hereda)
		AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
		adopter.setEmail("adopter@test.com");
		entityManager.persist(adopter);

		// creamos la adopción vinculada a ESE adoptante
		AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
		adoption.setAdopter(adopter); // Ahora sí compila porque los tipos coinciden
		entityManager.persist(adoption);
		
		entityManager.flush(); // sincronizamos con H2

		// intentamos borrar al adoptante a traves del service
		assertThrows(IllegalOperationException.class, () -> {
			userService.deleteUser(adopter.getId());
		});
	}
}
@Service
class MockUserService extends UserService {
    @Autowired
    private TestEntityManager entityManager;

    @Override
    protected void validateDeletion(Long userId) throws IllegalOperationException {
        // Buscamos manualmente en el EntityManager si hay una AdoptionEntity vinculada
        String query = "SELECT COUNT(a) FROM AdoptionEntity a WHERE a.adopter.id = :userId";
        Long count = (Long) entityManager.getEntityManager()
                .createQuery(query)
                .setParameter("userId", userId)
                .getSingleResult();

        if (count > 0) {
            throw new IllegalOperationException("Cannot delete user: Active processes found.");
        }
    }
}
@jakarta.persistence.Entity
class MockUser extends UserEntity {}
