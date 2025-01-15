package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.response.FeedbackTypeResponse;

import java.util.List;

public interface FeedbackTypesServiceInterface {
    FeedbackTypes createFeedbackType(FeedbackTypesDto feedbackTypesDto);
    FeedbackTypes getFeedbackTypeById(long id);
    List<FeedbackTypes>  getAllFeedbackTypes();
    FeedbackTypes updateFeedbackType(long id,FeedbackTypesDto feedbackTypesDto);
    void deleteFeedbackType(long id);
}
