package flycatch.feedback.service.feedBackTypes;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.repository.FeedBackTypesRepository;
import flycatch.feedback.response.FeedbackTypeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackTypesService implements FeedbackTypesServiceInterface{

    private final FeedBackTypesRepository feedBackTypesRepository;

    @Override
    public FeedbackTypeResponse createFeedbackType(FeedbackTypesDto feedbackTypesDto){
        log.info("Creating a new feedback type: {}",feedbackTypesDto.getName());
        FeedbackTypes feedbackTypes = new FeedbackTypes();
        feedbackTypes.setName(feedbackTypesDto.getName());
        FeedbackTypes savedFeedbackType = feedBackTypesRepository.save(feedbackTypes);
        FeedbackTypesDto createdDto = mapToDto(savedFeedbackType);
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type created successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackType(createdDto)
                        .build())
                .build();
    }

    @Override
    public FeedbackTypeResponse getFeedbackTypeById(long id){
        log.info("Fetching feedback type with id: {}",id);
        FeedbackTypes feedbackTypes = feedBackTypesRepository.findById(id)
                .orElseThrow(()->new AppException("Feedback type not found with id: "+id));
        FeedbackTypesDto feedbackTypesDto = mapToDto(feedbackTypes);
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type created successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackType(feedbackTypesDto)
                        .build())
                .build();
    }

    @Override
    public FeedbackTypeResponse getAllFeedbackTypes(){
        log.info("Fetching all feedback types");
        List<FeedbackTypesDto> feedbackTypesDtos = feedBackTypesRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("All feedback types retrieved successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(feedbackTypesDtos)
                        .build())
                .build();
    }

    @Override
    public FeedbackTypeResponse updateFeedbackType(long id,FeedbackTypesDto feedbackTypesDto){
        log.info("Updating feedback type with id: {}",id);
        FeedbackTypes feedbackTypes = feedBackTypesRepository.findById(id)
                .orElseThrow(()->new AppException("Feedback type not found with id: "+id));
        feedbackTypes.setName(feedbackTypesDto.getName());
        FeedbackTypes updateFeedbackType = feedBackTypesRepository.save(feedbackTypes);
        FeedbackTypesDto updatedDto =  mapToDto(updateFeedbackType);
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type updated successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackType(updatedDto)
                        .build())
                .build();
    }

    @Override
    public FeedbackTypeResponse deleteFeedbackType(long id){
        log.info("Deleting feedback type with id: {}",id);
        FeedbackTypes feedbackTypes = feedBackTypesRepository.findById(id)
                .orElseThrow(()-> new AppException("Feedback type not found with id: "+id));
        feedBackTypesRepository.delete(feedbackTypes);
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type deleted successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .message("Feedback type with id " + id + " successfully deleted.")
                        .build())
                .build();
    }

    private FeedbackTypesDto mapToDto(FeedbackTypes feedbackTypes){
        return new FeedbackTypesDto(feedbackTypes.getId(),feedbackTypes.getName());
    }
}
