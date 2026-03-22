package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AdopterRepository adopterRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AdoptionRepository adoptionRepository;

    private void validateReview(ReviewEntity review) throws IllegalOperationException {
        if (review == null)
            throw new IllegalOperationException("Review data cannot be null");

        if (review.getRating() == null)
            throw new IllegalOperationException("Rating cannot be null");

        if (review.getRating() < 1 || review.getRating() > 5)
            throw new IllegalOperationException("Rating must be between 1 and 5");

        if (review.getDate() == null)
            throw new IllegalOperationException("Review date cannot be null");

        if (review.getAdopter() == null || review.getAdopter().getId() == null)
            throw new IllegalOperationException("Review must have an adopter");

        if (review.getPet() == null || review.getPet().getId() == null)
            throw new IllegalOperationException("Review must be associated to a pet");
    }

    @Transactional
    public ReviewEntity createReview(ReviewEntity review)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Creating review for adopter {} and pet {}",
                review.getAdopter().getId(), review.getPet().getId());

        validateReview(review);

        if (!adopterRepository.existsById(review.getAdopter().getId()))
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		if (!petRepository.existsById(review.getPet().getId())) {
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
		}

		boolean hasCompletedAdoption = adoptionRepository.existsByAdopterIdAndPetId(
				review.getAdopter().getId(), review.getPet().getId());

		if (!hasCompletedAdoption) {
			throw new IllegalOperationException(
					"A review can only be created after completing an adoption process");
		}

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviews() {
        return reviewRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ReviewEntity getReview(Long reviewId) throws EntityNotFoundException {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviewsByPet(Long petId) throws EntityNotFoundException {
        if (!petRepository.existsById(petId))
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
        return reviewRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public List<ReviewEntity> getReviewsByAdopter(Long adopterId) throws EntityNotFoundException {
        if (!adopterRepository.existsById(adopterId))
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
        return reviewRepository.findByAdopterId(adopterId);
    }

    @Transactional
    public ReviewEntity updateReview(Long reviewId, Long requestingAdopterId, ReviewEntity updatedReview)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Updating review with id = {}", reviewId);

        ReviewEntity existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND));

        if (!existing.getAdopter().getId().equals(requestingAdopterId)) {
            throw new IllegalOperationException("Only the author can update this review");
        }

        validateReview(updatedReview);
        
        // Mantener integridad: No se puede cambiar el pet ni el adopter de una reseña ya creada
        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setDate(updatedReview.getDate());

        return reviewRepository.save(existing);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long requestingAdopterId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Deleting review with id = {}", reviewId);

        ReviewEntity existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND));

        if (!existing.getAdopter().getId().equals(requestingAdopterId)) {
            throw new IllegalOperationException("Only the author can delete this review");
        }

        reviewRepository.deleteById(reviewId);
    }
}
