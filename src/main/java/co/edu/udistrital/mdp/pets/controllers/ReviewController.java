package co.edu.udistrital.mdp.pets.controllers;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReviewService;
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDetailDTO createReview(@RequestBody ReviewDetailDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity entity = modelMapper.map(dto, ReviewEntity.class);
        return modelMapper.map(reviewService.createReview(entity), ReviewDetailDTO.class);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewDTO> getReviews() {
        List<ReviewEntity> entities = reviewService.getReviews();
        return modelMapper.map(entities, new TypeToken<List<ReviewDTO>>() {}.getType());
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDetailDTO getReview(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(reviewService.getReview(id), ReviewDetailDTO.class);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDetailDTO updateReview(@PathVariable Long id,
            @RequestParam Long requestingAdopterId,
            @RequestBody ReviewDetailDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity entity = modelMapper.map(dto, ReviewEntity.class);
        return modelMapper.map(reviewService.updateReview(id, requestingAdopterId, entity), ReviewDetailDTO.class);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id, @RequestParam Long requestingAdopterId)
            throws EntityNotFoundException, IllegalOperationException {
        reviewService.deleteReview(id, requestingAdopterId);
    }
}
