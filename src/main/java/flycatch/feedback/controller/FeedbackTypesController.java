package flycatch.feedback.controller;

import flycatch.feedback.dto.CreateFeedbackTypeDto;
import flycatch.feedback.dto.EmailDto;
import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.dto.UpdateFeedbackTypesDto;
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
    public ResponseEntity<FeedbackTypeResponse> createFeedbackType(@RequestBody CreateFeedbackTypeDto createFeedbackTypeDto) {
        FeedbackTypes feedbackType = feedbackTypesService.createFeedbackType(createFeedbackTypeDto);
        FeedbackTypesDto createdDto = convertToDto(feedbackType);

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.CREATED,
                "Feedback type created successfully.",
                List.of(createdDto),
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> getFeedbackTypeById(@PathVariable long id) {
        FeedbackTypes feedbackType = feedbackTypesService.getFeedbackTypeById(id);
        FeedbackTypesDto feedbackTypeDto = convertToDto(feedbackType);

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.OK,
                "Feedback type retrieved successfully.",
                List.of(feedbackTypeDto),
                null,
                null
        );

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

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.OK,
                "Feedback types retrieved successfully.",
                feedbackTypesDtos,
                feedbackTypesPage.getTotalPages(),
                feedbackTypesPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> updateFeedbackType(
            @PathVariable long id,
            @RequestBody UpdateFeedbackTypesDto updateFeedbackTypesDto) {

        FeedbackTypes feedbackType = feedbackTypesService.updateFeedbackType(id, updateFeedbackTypesDto);
        FeedbackTypesDto updatedDto = convertToDto(feedbackType);

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.OK,
                "Feedback type updated successfully.",
                List.of(updatedDto),
                null,
                null
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> deleteFeedbackType(@PathVariable long id) {
        feedbackTypesService.deleteFeedbackType(id);

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.OK,
                "Feedback type deleted successfully.",
                null,
                null,
                null
        );

        return ResponseEntity.ok(response);
    }

    private FeedbackTypesDto convertToDto(FeedbackTypes feedbackTypes) {
        List<EmailDto> emailsDto = feedbackTypes.getEmails().stream()
                .map(email -> new EmailDto(email.getEmail()))
                .collect(Collectors.toList());
        return new FeedbackTypesDto(feedbackTypes.getId(), feedbackTypes.getName(), emailsDto);
    }

    private FeedbackTypeResponse buildFeedbackTypeResponse(
            HttpStatus status,
            String message,
            List<FeedbackTypesDto> feedbackTypesDtos,
            Integer totalPages,
            Long totalElements) {

        FeedbackTypeResponse.Data data = FeedbackTypeResponse.Data.builder()
                .feedbackTypes(feedbackTypesDtos)
                .build();

        return FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message)
                .data(data)
                .build();
    }
}
