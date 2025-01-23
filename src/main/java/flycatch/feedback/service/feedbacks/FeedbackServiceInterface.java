package flycatch.feedback.service.feedbacks;

import flycatch.feedback.dto.FeedBackDto;
import flycatch.feedback.model.FeedBack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackServiceInterface {

    FeedBack createFeedback(FeedBackDto feedBackDto);
    FeedBack getFeedbackById(long id);
    Page<FeedBack> getAllFeedbacks(String search, Integer feedbackType, Pageable pageable);
    FeedBack updateFeedback(Long id,FeedBackDto feedBackDto);
    void deleteByAircraftId(Long aircraftId);
}
