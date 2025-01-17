package flycatch.feedback.controller;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.response.FeedbackTypeResponse;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

        FeedbackTypeResponse response = FeedbackTypeResponse.builder()
                .timestamp(java.time.LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type created successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(List.of(createdDto))
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> getFeedbackTypeById(@PathVariable long id) {
        FeedbackTypes feedbackType = feedbackTypesService.getFeedbackTypeById(id);
        FeedbackTypesDto feedbackTypeDto = convertToDto(feedbackType);

        FeedbackTypeResponse response = FeedbackTypeResponse.builder()
                .timestamp(java.time.LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type retrieved successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(List.of(feedbackTypeDto))
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<FeedbackTypeResponse> getAllFeedbackTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {

        Page<FeedbackTypes> feedbackTypesPage = feedbackTypesService.getAllFeedbackTypes(
                name,
                PageRequest.of(page, size)
        );

        List<FeedbackTypesDto> feedbackTypesDtos = feedbackTypesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        FeedbackTypeResponse response = FeedbackTypeResponse.builder()
                .timestamp(java.time.LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback types retrieved successfully.")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(feedbackTypesDtos)
                        .build())
                .totalPages(feedbackTypesPage.getTotalPages())
                .totalElements(feedbackTypesPage.getTotalElements())
                .build();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> updateFeedbackType(
            @PathVariable long id,
            @RequestBody FeedbackTypesDto feedbackTypesDto) {

        FeedbackTypes feedbackType = feedbackTypesService.updateFeedbackType(id, feedbackTypesDto);
        FeedbackTypesDto updatedDto = convertToDto(feedbackType);

        FeedbackTypeResponse response = FeedbackTypeResponse.builder()
                .timestamp(java.time.LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Feedback type updated successfully")
                .data(FeedbackTypeResponse.Data.builder()
                        .feedbackTypes(List.of(updatedDto))
                        .build())
                .build();

        return ResponseEntity.ok(response);
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
}
