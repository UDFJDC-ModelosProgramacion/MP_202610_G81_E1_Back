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
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
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
	void testCreateUserDuplicateEmailFails() {
		UserEntity user2 = factory.manufacturePojo(MockUser.class);
		user2.setEmail(data.get(0).getEmail()); // Ya existe en insertData

		assertThrows(IllegalOperationException.class, () -> userService.createUser(user2));
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
    void testCreateUserWithNullEntity() {
        assertThrows(IllegalOperationException.class, () -> {
            userService.createUser(null);
        });
    }

    @Test
    void testCreateUserWithBlankName() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("   ");
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone("1234567890");
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithBlankEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail("   ");
            newEntity.setPhone("1234567890");
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithBlankPhone() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone("   ");
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithBlankPassword() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone("1234567890");
            newEntity.setPassword("   ");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithNullName() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName(null);
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone("1234567890");
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithNullEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail(null);
            newEntity.setPhone("1234567890");
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithNullPhone() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone(null);
            newEntity.setPassword("password");
            userService.createUser(newEntity);
        });
    }

    @Test
    void testCreateUserWithNullPassword() {
        assertThrows(IllegalOperationException.class, () -> {
            UserEntity newEntity = factory.manufacturePojo(MockUser.class);
            newEntity.setName("Valid Name");
            newEntity.setEmail("valid@email.com");
            newEntity.setPhone("1234567890");
            newEntity.setPassword(null);
            userService.createUser(newEntity);
        });
    }

    @Test
    void testGetUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUser(999L);
        });
    }

    @Test
    void testUpdateUserWithNullEntity() {
        UserEntity entity = data.get(0);
        assertThrows(IllegalOperationException.class, () -> {
            userService.updateUser(entity.getId(), null); // Deberia lanzar excepcion si user es null
        });
    }

    @Test
    void testUpdateUserNotFound() {
        UserEntity pojo = factory.manufacturePojo(MockUser.class);
        pojo.setEmail("nonexistent@test.com");
        pojo.setPhone("1111111111");
        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateUser(999L, pojo);
        });
    }
    
    @Test
    void testDeleteUserNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteUser(999L);
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
	void testUpdateUserEmailAlreadyExistsInAnotherRecord() {
		// 1. Limpiamos para evitar que los datos del @BeforeEach interfieran con la consulta de unicidad
		entityManager.getEntityManager().createQuery("delete from UserEntity").executeUpdate();

		// 2. Creamos dos usuarios distintos usando la clase de Mock
		UserEntity user1 = factory.manufacturePojo(MockUser.class);
		user1.setEmail("original@test.com");
		user1.setPhone("111111");
		entityManager.persist(user1);

		UserEntity user2 = factory.manufacturePojo(MockUser.class);
		user2.setEmail("ocupado@test.com");
		user2.setPhone("222222");
		entityManager.persist(user2);
		
		entityManager.flush();
		entityManager.clear(); // Limpiamos el caché de primer nivel para forzar consulta a BD

		// 3. Intentamos actualizar el user1 con el email que ya tiene el user2
		UserEntity updateData = factory.manufacturePojo(MockUser.class);
		updateData.setEmail("ocupado@test.com"); 
		updateData.setPhone("111111");
		updateData.setName("Nombre Valido");
		updateData.setPassword("Pass123");

		// Ahora sí debería lanzar IllegalOperationException porque la validación de negocio
		// encontrará el email antes de que JPA lance un error de persistencia
		assertThrows(IllegalOperationException.class, () -> {
			userService.updateUser(user1.getId(), updateData);
		});
	}

    @Test
    void testValidateUserInvalidEmailFormat() {
        UserEntity user = factory.manufacturePojo(MockUser.class);
        user.setEmail("email_sin_arroba.com"); // Formato que hará fallar el Regex

        assertThrows(IllegalOperationException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void testValidateUserInvalidPhoneFormat() {
        UserEntity user = factory.manufacturePojo(MockUser.class);
        user.setEmail("valido@test.com");
        user.setPhone("12345abc"); // Contiene letras, hará fallar el Regex de [0-9]+

        assertThrows(IllegalOperationException.class, () -> {
            userService.createUser(user);
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
