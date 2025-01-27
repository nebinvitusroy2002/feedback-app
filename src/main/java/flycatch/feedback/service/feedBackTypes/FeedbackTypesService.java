package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.CreateFeedbackTypeDto;
import flycatch.feedback.dto.EmailDto;
import flycatch.feedback.dto.UpdateFeedbackTypesDto;
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
    public FeedbackTypes createFeedbackType(CreateFeedbackTypeDto createFeedbackTypeDto) {
        log.info("Creating a new feedback type: {}", createFeedbackTypeDto.getName());
        FeedbackTypes feedbackTypes = new FeedbackTypes();
        feedbackTypes.setName(createFeedbackTypeDto.getName());
        try {
            return feedBackTypesRepository.save(feedbackTypes);
        } catch (Exception ex) {
            log.error("Unexpected error while creating feedback type: {}", ex.getMessage());
            throw new AppException("feedback.type.create.error");
        }
    }

    @Override
    public FeedbackTypes getFeedbackTypeById(long id) {
        log.info("Fetching feedback type with id: {}", id);
        try {
            return feedBackTypesRepository.findById(id)
                    .orElseThrow(() -> new AppException("feedback.type.not.found",String.valueOf(id)));
        } catch (Exception ex) {
            log.error("Unexpected error while fetching feedback type: {}", ex.getMessage());
            throw new AppException("feedback.type.not.found");
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
            throw new AppException("feedback.type.search.error");
        }
    }

    @Override
    @Transactional
    public FeedbackTypes updateFeedbackType(long id, UpdateFeedbackTypesDto updateFeedbackTypesDto) {
        log.info("Updating feedback type with id: {}", id);

        FeedbackTypes feedbackType = feedBackTypesRepository.findById(id)
                .orElseThrow(() -> new AppException("feedback.type.not.found", String.valueOf(id)));

        feedbackType.setName(updateFeedbackTypesDto.getName());
        if (updateFeedbackTypesDto.getEmails() != null && !updateFeedbackTypesDto.getEmails().isEmpty()) {
            List<Email> emailList = new ArrayList<>();
            for (EmailDto dto : updateFeedbackTypesDto.getEmails()) {
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
            throw new AppException("feedback.type.update.error");
        }
    }

    @Override
    public void deleteFeedbackType(long id) {
        log.info("Deleting feedback type with id: {}", id);
        try {
            feedBackTypesRepository.deleteById(id);
            log.info("Feedback type with id: {} deleted successfully", id);
        } catch (Exception ex) {
            log.error("Unexpected error while deleting feedback type: {}", ex.getMessage());
            throw new AppException("feedback.type.delete.error");
        }
    }
}
