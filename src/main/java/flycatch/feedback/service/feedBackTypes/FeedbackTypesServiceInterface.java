package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.response.FeedbackTypeResponse;

import java.util.List;

public interface FeedbackTypesServiceInterface {
    FeedbackTypeResponse createFeedbackType(FeedbackTypesDto feedbackTypesDto);
    FeedbackTypeResponse getFeedbackTypeById(long id);
    FeedbackTypeResponse getAllFeedbackTypes();
    FeedbackTypeResponse updateFeedbackType(long id,FeedbackTypesDto feedbackTypesDto);
    FeedbackTypeResponse deleteFeedbackType(long id);
}
