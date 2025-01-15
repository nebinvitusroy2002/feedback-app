package flycatch.feedback.controller;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.response.FeedbackTypeResponse;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback-types")
@RequiredArgsConstructor
public class FeedbackTypesController {

    private final FeedbackTypesService feedbackTypesService;

    @PostMapping
    public ResponseEntity<FeedbackTypeResponse> createFeedbackType(@RequestBody FeedbackTypesDto feedbackTypesDto) {
        FeedbackTypes feedbackType = feedbackTypesService.createFeedbackType(feedbackTypesDto);
        FeedbackTypesDto createdDto = convertToDto(feedbackType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildResponse(createdDto, "Feedback type created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> getFeedbackTypeById(@PathVariable long id) {
        FeedbackTypes feedbackType = feedbackTypesService.getFeedbackTypeById(id);
        FeedbackTypesDto feedbackTypeDto = convertToDto(feedbackType);
        return ResponseEntity.ok(buildResponse(feedbackTypeDto, "Feedback type retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<FeedbackTypeResponse> getAllFeedbackTypes() {
        List<FeedbackTypes> feedbackTypes = feedbackTypesService.getAllFeedbackTypes();
        List<FeedbackTypesDto> feedbackTypesDtos = feedbackTypes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(buildResponse(feedbackTypesDtos, "All feedback types retrieved successfully"));
    }


    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> updateFeedbackType(
            @PathVariable long id,
            @RequestBody FeedbackTypesDto feedbackTypesDto) {
        FeedbackTypes feedbackType = feedbackTypesService.updateFeedbackType(id, feedbackTypesDto);
        FeedbackTypesDto updatedDto = convertToDto(feedbackType);
        return ResponseEntity.ok(buildResponse(updatedDto, "Feedback type updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> deleteFeedbackType(@PathVariable long id) {
        feedbackTypesService.deleteFeedbackType(id);
        FeedbackTypeResponse response = FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback Type deleted successfully.")
                .build();
        return ResponseEntity.ok(response);

    }

    private FeedbackTypesDto convertToDto(FeedbackTypes feedbackTypes) {
        return new FeedbackTypesDto(feedbackTypes.getId(), feedbackTypes.getName());
    }

    private FeedbackTypeResponse buildResponse(Object data, String message) {
        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message(message)
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(data instanceof List ? (List<FeedbackTypesDto>) data : List.of((FeedbackTypesDto) data))
                        .build())
                .build();
    }
}
