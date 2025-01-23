package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.EmailDto;
import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.Email;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackTypesRepository;
import flycatch.feedback.search.SearchCriteria;
import flycatch.feedback.specification.FeedbackTypesSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackTypesService implements FeedbackTypesServiceInterface {

    private final FeedBackTypesRepository feedBackTypesRepository;

    @Override
    public FeedbackTypes createFeedbackType(FeedbackTypesDto feedbackTypesDto) {
        log.info("Creating a new feedback type: {}", feedbackTypesDto.getName());
        FeedbackTypes feedbackTypes = new FeedbackTypes();
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
    @Transactional
    public FeedbackTypes updateFeedbackType(long id, FeedbackTypesDto feedbackTypesDto) {
        log.info("Updating feedback type with id: {}", id);

        FeedbackTypes feedbackType = feedBackTypesRepository.findById(id)
                .orElseThrow(() -> new AppException("Feedback type not found with id: " + id));

        feedbackType.setName(feedbackTypesDto.getName());
        if(feedbackTypesDto.getEmails()!=null && !feedbackTypesDto.getEmails().isEmpty()){
            log.info("not empty");
            List<Email> emailList = new ArrayList<>();
        for(EmailDto  dto: feedbackTypesDto.getEmails()) {
            emailList.add(Email.builder()
                    .email(dto.getEmail())
                    .feedbackType(feedbackType)
                    .build());
        }
            feedbackType.setEmails(emailList);
        }

        try {
            return feedBackTypesRepository.save(feedbackType);
        } catch (Exception ex) {
            log.error("Error occurred while updating feedback type: {}", ex.getMessage(), ex);
            throw new AppException("Failed to update feedback type: " + ex.getMessage());
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
