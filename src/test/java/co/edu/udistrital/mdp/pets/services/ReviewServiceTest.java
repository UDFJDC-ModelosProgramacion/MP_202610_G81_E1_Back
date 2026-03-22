package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReviewService.class)
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<ReviewEntity> data = new ArrayList<>();
    private AdopterEntity adopter;
    private PetEntity pet;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ReviewEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        // 1. Crear Adoptante
        adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        // 2. Crear Mascota
        pet = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(pet);

        // 3. Crear la Adopción (Requisito indispensable para el Service)
        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setAdopter(adopter);
        adoption.setPet(pet);
        adoption.setAdoptionDate(LocalDate.now()); 
        entityManager.persist(adoption);

        // 4. Crear datos de prueba para listas
        for (int i = 0; i < 3; i++) {
            ReviewEntity entity = factory.manufacturePojo(ReviewEntity.class);
            entity.setAdopter(adopter);
            entity.setPet(pet);
            entity.setRating(5);
            entity.setDate(LocalDate.now()); 
            
            entityManager.persist(entity);
            data.add(entity);
        }
        entityManager.flush();
    }

    // --- TESTS DE CREACIÓN ---

    @Test
    void createReviewSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity newReview = new ReviewEntity();
        newReview.setRating(5);
        newReview.setComment("Increíble experiencia de adopción");
        newReview.setDate(LocalDate.now());
        newReview.setAdopter(adopter);
        newReview.setPet(pet);

        ReviewEntity result = reviewService.createReview(newReview);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(5, result.getRating());
        assertEquals(adopter.getId(), result.getAdopter().getId());
    }

    @Test
    void createReviewWithoutAdoptionTest() {
        // Crear un adoptante que NO tiene una adopción registrada para este pet
        AdopterEntity randomAdopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(randomAdopter);

        ReviewEntity illegalReview = new ReviewEntity();
        illegalReview.setRating(5);
        illegalReview.setDate(LocalDate.now());
        illegalReview.setAdopter(randomAdopter);
        illegalReview.setPet(pet);

        // Debe fallar porque el service valida la existencia de la adopción
        assertThrows(IllegalOperationException.class, () -> reviewService.createReview(illegalReview));
    }

    @Test
    void createReviewInvalidRatingTest() {
        ReviewEntity badReview = new ReviewEntity();
        badReview.setRating(0); // Fuera de rango (1-5)
        badReview.setAdopter(adopter);
        badReview.setPet(pet);
        badReview.setDate(LocalDate.now());

        assertThrows(IllegalOperationException.class, () -> reviewService.createReview(badReview));
    }

    // --- TESTS DE ACTUALIZACIÓN ---

    @Test
    void updateReviewSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity existing = data.get(0);
        
        ReviewEntity updateData = new ReviewEntity();
        updateData.setRating(1);
        updateData.setComment("Cambié de opinión sobre el comportamiento");
        updateData.setDate(LocalDate.now());
        updateData.setAdopter(adopter);
        updateData.setPet(pet);

        ReviewEntity result = reviewService.updateReview(existing.getId(), adopter.getId(), updateData);
        
        assertEquals(1, result.getRating());
        assertEquals("Cambié de opinión sobre el comportamiento", result.getComment());
    }

    @Test
    void updateReviewUnauthorizedTest() {
        ReviewEntity existing = data.get(0);
        // Intentar actualizar con un ID de adoptante que no es el dueño (999L)
        assertThrows(IllegalOperationException.class, () -> 
            reviewService.updateReview(existing.getId(), 999L, existing)
        );
    }

    // --- TESTS DE BÚSQUEDA ---

    @Test
    void getReviewsByPetTest() throws EntityNotFoundException {
        List<ReviewEntity> results = reviewService.getReviewsByPet(pet.getId());
        assertFalse(results.isEmpty());
        assertEquals(data.size(), results.size());
    }

    @Test
    void getReviewNotFoundTest() {
        assertThrows(EntityNotFoundException.class, () -> reviewService.getReview(999L));
    }

    // --- TESTS DE BORRADO ---

    @Test
    void deleteReviewSuccessTest() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity target = data.get(0);
        reviewService.deleteReview(target.getId(), adopter.getId());
        
        ReviewEntity deleted = entityManager.find(ReviewEntity.class, target.getId());
        assertNull(deleted);
    }

    @Test
    void deleteReviewUnauthorizedTest() {
        ReviewEntity target = data.get(0);
        assertThrows(IllegalOperationException.class, () -> 
            reviewService.deleteReview(target.getId(), 999L)
        );
    }
}
