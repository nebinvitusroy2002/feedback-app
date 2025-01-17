package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackTypesRepository;
import flycatch.feedback.search.SearchCriteria;
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
        FeedbackTypes feedbackTypes = new FeedbackTypes();
        feedbackTypes.setId(feedbackTypesDto.getId());
        feedbackTypes.setName(feedbackTypesDto.getName());
        try {
            return feedBackTypesRepository.save(feedbackTypes);
        }catch (Exception ex) {
            log.error("Unexpected error while creating feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while creating feedback type. Please try again later.");
        }
    }

    @Override
    public FeedbackTypes getFeedbackTypeById(long id) {
        log.info("Fetching feedback type with id: {}", id);
        try {
            return feedBackTypesRepository.findById(id)
                    .orElseThrow(() -> new AppException("Feedback type not found with id: " + id));
        }catch (Exception ex) {
            log.error("Unexpected error while fetching feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while fetching feedback type. Please try again later.");
        }
    }

    @Override
    public Page<FeedbackTypes> getAllFeedbackTypes(String name, Pageable pageable) {
        log.info("Searching feedback types with name: {}", name);

        Specification<FeedbackTypes> specification = Specification.where(null);

        if (name != null && !name.trim().isEmpty()) {
            SearchCriteria nameCriteria = new SearchCriteria("name", ":", name);
            specification = specification.and(new FeedbackTypesSpecification(nameCriteria));
        }

        try {
            return feedBackTypesRepository.findAll(specification, pageable);
        } catch (Exception ex) {
            log.error("Error while searching feedback types: {}", ex.getMessage());
            throw new AppException("Unable to search feedback types. Please try again later.");
        }
    }


    @Override
    public FeedbackTypes updateFeedbackType(long id, FeedbackTypesDto feedbackTypesDto) {
        log.info("Updating feedback type with id: {}", id);
        FeedbackTypes feedbackTypes = feedBackTypesRepository.findById(id)
                .orElseThrow(() -> new AppException("Feedback type not found with id: " + id));
        feedbackTypes.setName(feedbackTypesDto.getName());
        try {
            return feedBackTypesRepository.save(feedbackTypes);
        }catch (Exception ex) {
            log.error("Unexpected error while updating feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while updating feedback type. Please try again later.");
        }
    }

    @Override
    public void deleteFeedbackType(long id) {
        log.info("Deleting feedback type with id: {}", id);
        try {
            feedBackTypesRepository.deleteById(id);
            log.info("Feedback type with id: {} deleted successfully", id);
        }catch (Exception ex) {
            log.error("Unexpected error while deleting feedback type: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred. Please try again later.");
        }
    }
}
