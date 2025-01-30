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
public class FeedBackService implements FeedbackServiceInterface {

    private final FeedbackTypesService feedbackTypesService;
    private final AircraftService aircraftService;
    private final FeedBackRepository feedBackRepository;
    private final EmailService emailService;

    public FeedBack createFeedback(FeedBackDto feedBackDto) {
        log.info("Creating new feedback for aircraft: {}", feedBackDto.getAircraftId());

        Aircraft aircraft = aircraftService.getAircraftById(feedBackDto.getAircraftId());
        FeedbackTypes feedbackTypes = feedbackTypesService.getFeedbackTypeById(feedBackDto.getFeedbackTypeId());

        FeedBack feedBack = new FeedBack();
        feedBack.setFeedbackText(feedBackDto.getFeedbackText());
        feedBack.setFeedbackType(feedbackTypes);
        feedBack.setAircraft(aircraft);

        FeedBack savedFeedback;
        try {
            savedFeedback = feedBackRepository.save(feedBack);
        } catch (Exception e) {
            log.error("Error while creating feedback: {}", e.getMessage());
            throw new AppException("feedback.create.error");
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
                throw new AppException("feedback.create.email.error");
            }
        } else {
            log.warn("No email addresses found for feedback type: {}", feedbackTypes.getId());
            throw new AppException("feedback.create.email.warning");
        }
        return savedFeedback;
    }

    public FeedBack getFeedbackById(long id) {
        log.info("Fetching feedback with id: {}", id);

        try {
            return feedBackRepository.findById(id)
                    .orElseThrow(() -> new AppException("feedback.fetch.notfound", String.valueOf(id)));
        } catch (Exception e) {
            log.error("Error while fetching feedback: {}", e.getMessage());
            throw new AppException("feedback.fetch.notfound");
        }
    }

    public Page<FeedBack> getAllFeedbacks(String text, Integer typeId, Pageable pageable) {
        Specification<FeedBack> specification = Specification.where(null);

        if (text != null && !text.isEmpty()) {
            SearchCriteria textCriteria = new SearchCriteria("feedbackText", ":", text);
            specification = specification.and(new FeedbackSpecification(textCriteria));
        }

        if (typeId != null) {
            SearchCriteria typeCriteria = new SearchCriteria("feedbackType.id", ":", typeId);
            specification = specification.and(new FeedbackSpecification(typeCriteria));
        }

        try {
            return feedBackRepository.findAll(specification, pageable);
        } catch (Exception e) {
            log.error("Error while getting feedbacks: {}", e.getMessage());
            throw new AppException("feedback.search.error");
        }
    }

    public FeedBack updateFeedback(Long id, FeedBackDto feedBackDto) {
        log.info("Updating feedback with id: {}", id);

        FeedBack feedBack = feedBackRepository.findById(id)
                .orElseThrow(() -> new AppException("feedback.fetch.notfound", String.valueOf(id)));

        if (feedBackDto.getFeedbackText() != null){
            feedBack.setFeedbackText(feedBackDto.getFeedbackText());
        }
        if (feedBackDto.getFeedbackTypeId() != 0){
            FeedbackTypes feedbackTypes = feedbackTypesService.getFeedbackTypeById(feedBackDto.getFeedbackTypeId());
            feedBack.setFeedbackType(feedbackTypes);
        }
        if (feedBackDto.getAircraftId() != 0){
            Aircraft aircraft = aircraftService.getAircraftById(feedBackDto.getAircraftId());
            feedBack.setAircraft(aircraft);
        }

        try {
            return feedBackRepository.save(feedBack);
        } catch (Exception e) {
            log.error("Error while updating feedback: {}", e.getMessage());
            throw new AppException("feedback.update.error");
        }
    }

    public void deleteByFeedbackId(Long feedbackId) {
        log.info("Deleting feedback with id: {}", feedbackId);

        FeedBack feedBack = feedBackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException("feedback.delete.notfound", String.valueOf(feedbackId)));

        try {
            feedBackRepository.delete(feedBack);
            log.info("Feedback deleted successfully for feedback id: {}", feedbackId);
        } catch (Exception e) {
            log.error("Error while deleting feedback: {}", e.getMessage());
            throw new AppException("feedback.delete.error");
        }
    }

    public byte[] exportFeedbackReport(Long feedbackTypeId, Long aircraftId) {
        log.info("Exporting feedback report to byte array.");

        Specification<FeedBack> specification = Specification.where(null);

        if (feedbackTypeId != null) {
            SearchCriteria typeCriteria = new SearchCriteria("feedbackType.id", ":", feedbackTypeId);
            specification = specification.and(new FeedbackSpecification(typeCriteria));
        }

        if (aircraftId != null) {
            SearchCriteria aircraftIdCriteria = new SearchCriteria("aircraft.id", ":", aircraftId);
            specification = specification.and(new FeedbackSpecification(aircraftIdCriteria));
        }

        List<FeedBack> feedBacks = feedBackRepository.findAll(specification);
        List<ReportGenerationDto> reportData = feedBacks.stream()
                .map(feedBack -> new ReportGenerationDto(
                        feedBack.getId(),
                        feedBack.getFeedbackText(),
                        feedBack.getFeedbackType().getName(),
                        feedBack.getAircraft().getName(),
                        feedBack.getAircraft().getType()
                ))
                .collect(Collectors.toList());

        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("templates/feedback_report.xlsx");
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            if (templateInputStream == null) {
                throw new AppException("feedback.report.export.template.notfound");
            }
            Context context = new Context();
            context.putVar("feedbacks", reportData);
            JxlsHelper.getInstance().processTemplate(templateInputStream, os, context);
            log.info("Feedback report generated successfully.");
            return os.toByteArray();

        } catch (Exception e) {
            log.error("Error while generating feedback report: {}", e.getMessage(), e);
            throw new AppException("feedback.report.export.error");
        }
    }
}
