package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackTypesRepository;
import flycatch.feedback.specification.FeedbackTypesSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackTypesService implements FeedbackTypesServiceInterface {

    private final FeedBackTypesRepository feedBackTypesRepository;

    @Override
    public FeedbackTypes createFeedbackType(FeedbackTypesDto feedbackTypesDto) {
        log.info("Creating a new feedback type: {}", feedbackTypesDto.getName());
        try {
            FeedbackTypes feedbackTypes = new FeedbackTypes();
            feedbackTypes.setId(feedbackTypesDto.getId());
            feedbackTypes.setName(feedbackTypesDto.getName());
            return feedBackTypesRepository.save(feedbackTypes);
        } catch (DataAccessException ex) {
            log.error("Database error while creating feedback type: {}", ex.getMessage());
            throw new AppException("Unable to create feedback type. Please try again later.");
        } catch (Exception ex) {
            log.error("Unexpected error while creating feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred. Please try again later.");
        }
    }

    @Override
    public FeedbackTypes getFeedbackTypeById(long id) {
        log.info("Fetching feedback type with id: {}", id);
        try {
            return feedBackTypesRepository.findById(id)
                    .orElseThrow(() -> new AppException("Feedback type not found with id: " + id));
        } catch (DataAccessException ex) {
            log.error("Database error while fetching feedback type: {}", ex.getMessage());
            throw new AppException("Unable to fetch feedback type. Please try again later.");
        } catch (Exception ex) {
            log.error("Unexpected error while fetching feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred. Please try again later.");
        }
    }

    @Override
    public Page<FeedbackTypes> getAllFeedbackTypes(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isEmpty()) {
            return searchFeedbackTypesByName(search, pageable);
        } else {
            return getAllFeedbackTypes(pageable);
        }
    }
    private Page<FeedbackTypes> getAllFeedbackTypes(Pageable pageable) {
        log.info("Fetching all feedback types with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            return feedBackTypesRepository.findAll(pageable);
        } catch (DataAccessException ex) {
            log.error("Database error while fetching feedback types: {}", ex.getMessage());
            throw new AppException("Unable to fetch feedback types. Please try again later.");
        }
    }


    @Override
    public Page<FeedbackTypes> searchFeedbackTypesByName(String search, Pageable pageable) {
        log.info("Searching feedback types with name: {}", search);
        if (search == null || search.trim().isEmpty()) {
            log.warn("Search term is empty or null");
            throw new AppException("Search term cannot be empty or null.");
        }
        try {
            Specification<FeedbackTypes> spec = FeedbackTypesSpecification.hasNameEqual(search);
            Page<FeedbackTypes> feedbackTypesPage = feedBackTypesRepository.findAll(spec, pageable);
            if (feedbackTypesPage.getTotalElements() == 0) {
                log.warn("No feedback types found for search term: {}", search);
                throw new AppException("No feedback types found for the given search term.");
            }
            return feedbackTypesPage;
        } catch (DataAccessException ex) {
            log.error("Database error while searching feedback types: {}", ex.getMessage());
            throw new AppException("Unable to search feedback types. Please try again later.");
        }
    }


    @Override
    public FeedbackTypes updateFeedbackType(long id, FeedbackTypesDto feedbackTypesDto) {
        log.info("Updating feedback type with id: {}", id);
        try {
            FeedbackTypes feedbackTypes = feedBackTypesRepository.findById(id)
                    .orElseThrow(() -> new AppException("Feedback type not found with id: " + id));
            feedbackTypes.setName(feedbackTypesDto.getName());
            return feedBackTypesRepository.save(feedbackTypes);
        } catch (DataAccessException ex) {
            log.error("Database error while updating feedback type: {}", ex.getMessage());
            throw new AppException("Unable to update feedback type. Please try again later.");
        } catch (Exception ex) {
            log.error("Unexpected error while updating feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred. Please try again later.");
        }
    }

    @Override
    public void deleteFeedbackType(long id) {
        log.info("Deleting feedback type with id: {}", id);
        try {
            feedBackTypesRepository.deleteById(id);
            log.info("Feedback type with id: {} deleted successfully", id);
        } catch (DataAccessException ex) {
            log.error("Database error while deleting feedback type: {}", ex.getMessage());
            throw new AppException("Unable to delete feedback type. Please try again later.");
        } catch (Exception ex) {
            log.error("Unexpected error while deleting feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred. Please try again later.");
        }
    }
}
