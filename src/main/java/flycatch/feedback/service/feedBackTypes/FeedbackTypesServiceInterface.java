package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.CreateFeedbackTypeDto;
import flycatch.feedback.dto.UpdateFeedbackTypesDto;
import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FeedbackTypesServiceInterface {
    FeedbackTypes createFeedbackType(CreateFeedbackTypeDto createFeedbackTypeDto);
    FeedbackTypes getFeedbackTypeById(long id);
    Page<FeedbackTypes> getAllFeedbackTypes(String name, Pageable pageable);
    FeedbackTypes updateFeedbackType(long id, UpdateFeedbackTypesDto updateFeedbackTypesDto);
    void deleteFeedbackType(long id);
}
