package flycatch.feedback.service.feedbacks;

import flycatch.feedback.dto.FeedBackDto;
import flycatch.feedback.dto.ReportGenerationDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.model.Email;
import flycatch.feedback.model.FeedBack;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackRepository;
import flycatch.feedback.search.SearchCriteria;
import flycatch.feedback.service.aircraft.AircraftService;
import flycatch.feedback.service.email.EmailService;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import flycatch.feedback.specification.FeedbackSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FeedBackService implements FeedbackServiceInterface{

    private final FeedbackTypesService feedbackTypesService;
    private final AircraftService aircraftService;
    private final FeedBackRepository feedBackRepository;
    private final EmailService emailService;

    public FeedBack createFeedback(FeedBackDto feedBackDto){
        log.info("Creating new feedback for aircraft: {}",feedBackDto.getAircraftId());

        Aircraft aircraft = aircraftService.getAircraftById(feedBackDto.getAircraftId());
        FeedbackTypes feedbackTypes = feedbackTypesService.getFeedbackTypeById(feedBackDto.getFeedbackTypeId());

        FeedBack feedBack = new FeedBack();
        feedBack.setFeedbackText(feedBackDto.getFeedbackText());
        feedBack.setFeedbackType(feedbackTypes);
        feedBack.setAircraft(aircraft);

        FeedBack savedFeedback;
        try {
            savedFeedback = feedBackRepository.save(feedBack);
        }catch (Exception e){
            log.error("Error while creating feedback: {}",e.getMessage());
            throw new AppException("An unexpected error occurred while creating feedback. Please try again later.");
        }
        List<Email> emails = savedFeedback.getFeedbackType().getEmails();
        if (emails != null && !emails.isEmpty()) {
            List<String> emailAddresses = emails.stream()
                    .map(Email::getEmail)
                    .collect(Collectors.toList());

            try {
                emailService.sendNotification(savedFeedback.getFeedbackText(), emailAddresses);
                log.info("Email notifications sent successfully for feedback id: {}", savedFeedback.getId());
            } catch (Exception e) {
                log.error("Error while sending email notifications: {}", e.getMessage(), e);
                throw new AppException("Feedback created, but email notifications could not be sent.");
            }
        } else {
            log.warn("No email addresses found for feedback type: {}", feedbackTypes.getId());
        }
        return savedFeedback;
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

    public void deleteByFeedbackId(Long feedbackId) {
        log.info("Deleting feedbacks associated with feedback id: {}", feedbackId);
        feedBackRepository.findById(feedbackId);
        log.info("Feedbacks deleted successfully for feedback id: {}", feedbackId);
    }

    public byte[] exportFeedbackReport() {
        log.info("Exporting feedback report to byte array.");

        List<FeedBack> feedBacks = feedBackRepository.findAll();
        List<ReportGenerationDto> reportData = feedBacks.stream()
                .map(feedBack -> new ReportGenerationDto(
                        feedBack.getId(),
                        feedBack.getFeedbackText(),
                        feedBack.getFeedbackType().getName(),
                        feedBack.getAircraft().getName(),
                        feedBack.getAircraft().getType()
                ))
                .toList();

        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("templates/feedback_report.xlsx");
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            if (templateInputStream == null) {
                throw new AppException("Template file not found.");
            }

            Context context = new Context();
            context.putVar("feedbacks", reportData);

            JxlsHelper.getInstance().processTemplate(templateInputStream, os, context);
            log.info("Feedback report generated successfully.");

            return os.toByteArray();

        } catch (Exception e) {
            log.error("Error while generating feedback report: {}", e.getMessage(), e);
            throw new AppException("Failed to generate feedback report.");
        }
    }

}
