package flycatch.feedback.service.feedbacks;

import flycatch.feedback.dto.FeedBackDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.model.FeedBack;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackRepository;
import flycatch.feedback.search.SearchCriteria;
import flycatch.feedback.service.aircraft.AircraftService;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import flycatch.feedback.specification.FeedbackSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedBackService {

    private final FeedbackTypesService feedbackTypesService;
    private final AircraftService aircraftService;
    private final FeedBackRepository feedBackRepository;

    public FeedBack createFeedback(FeedBackDto feedBackDto){
        log.info("Creating new feedback for aircraft: {}",feedBackDto.getAircraftId());

        Aircraft aircraft = aircraftService.getAircraftById(feedBackDto.getAircraftId());
        FeedbackTypes feedbackTypes = feedbackTypesService.getFeedbackTypeById(feedBackDto.getFeedbackTypeId());

        FeedBack feedBack = new FeedBack();
        feedBack.setFeedbackText(feedBackDto.getFeedbackText());
        feedBack.setFeedbackType(feedbackTypes);
        feedBack.setAircraft(aircraft);

        try {
            return feedBackRepository.save(feedBack);
        }catch (Exception e){
            log.error("Error while creating feedback: {}",e.getMessage());
            throw new AppException("An unexpected error occurred while creating feedback. Please try again later.");
        }
    }

    public FeedBack getFeedbackById(long id){
        log.info("Fetching feedback with id: {}",id);

        try {
            return feedBackRepository.findById(id)
                    .orElseThrow(()->new AppException("Feedback not found with id: "+id));
        }catch (Exception e){
            log.error("Error while fetching feedback: {}",e.getMessage());
            throw new AppException("An unexpected error occurred while fetching feedback.Please try again later...");
        }
    }

    public Page<FeedBack> getAllFeedbacks(String search, Integer feedbackType, Pageable pageable) {
        Specification<FeedBack> specification = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            SearchCriteria textCriteria = new SearchCriteria("feedbackText", ":", search);
            specification = specification.and(new FeedbackSpecification(textCriteria));
        }

        if (feedbackType != null) {
            SearchCriteria typeCriteria = new SearchCriteria("feedbackType.id", ":", feedbackType);
            specification = specification.and(new FeedbackSpecification(typeCriteria));
        }

        try {
            return feedBackRepository.findAll(specification, pageable);
        } catch (Exception e) {
            log.error("Error while getting feedbacks: {}", e.getMessage());
            throw new AppException("Unable to get the result. Please try again later...");
        }
    }

    public FeedBack updateFeedback(Long id,FeedBackDto feedBackDto){
        log.info("Updating feedback with id: {}",id);

        FeedBack feedBack = feedBackRepository.findById(id)
                .orElseThrow(()->new AppException("Feedback not found with id: "+id));

        Aircraft aircraft = aircraftService.getAircraftById(feedBackDto.getAircraftId());
        FeedbackTypes feedbackTypes = feedbackTypesService.getFeedbackTypeById(feedBackDto.getFeedbackTypeId());

        feedBack.setFeedbackText(feedBackDto.getFeedbackText());
        feedBack.setFeedbackType(feedbackTypes);
        feedBack.setAircraft(aircraft);

        try {
            return feedBackRepository.save(feedBack);
        }catch (Exception e){
            log.error("Error while updating feedback: {}",e.getMessage());
            throw new AppException("Unable to update feedback. Please try again later");
        }
    }

    public void deleteFeedBack(long id){
        log.info("Deleting feedback with id: {}",id);
        feedBackRepository.findById(id)
                .orElseThrow(()->new AppException("Feedback not found with id: "+id));
        feedBackRepository.deleteById(id);
    }
}
