package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FeedbackTypesServiceInterface {
    FeedbackTypes createFeedbackType(FeedbackTypesDto feedbackTypesDto);
    FeedbackTypes getFeedbackTypeById(long id);
    Page<FeedbackTypes> getAllFeedbackTypes(String name, Pageable pageable);
    FeedbackTypes updateFeedbackType(long id,FeedbackTypesDto feedbackTypesDto);
    void deleteFeedbackType(long id);
}
