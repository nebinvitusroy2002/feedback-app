package flycatch.feedback.controller;

import flycatch.feedback.dto.*;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.response.feedbacktypes.FeedbackTypeResponse;
import flycatch.feedback.response.feedbacktypes.FeedbackTypesPagedResponse;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import flycatch.feedback.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
                List.of(createdDto)
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
                List.of(feedbackTypeDto)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<FeedbackTypesPagedResponse> getAllFeedbackTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Page<FeedbackTypes> feedbackTypesPage = feedbackTypesService.getAllFeedbackTypes(
                name,
                PageRequest.of(page, size,  SortUtil.getSort(sort))
        );

        List<FeedbackTypesDto> feedbackTypesDtos = feedbackTypesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = feedbackTypesDtos.isEmpty() ? "No feedback types found." : "Feedback types retrieved successfully.";

        FeedbackTypesPagedResponse response = buildPagedFeedbackTypesResponse(
                message,
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
                List.of(updatedDto)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> deleteFeedbackType(@PathVariable long id) {
        feedbackTypesService.deleteFeedbackType(id);

        FeedbackTypeResponse response = buildFeedbackTypeResponse(
                HttpStatus.OK,
                "Feedback type deleted successfully.",
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
            List<FeedbackTypesDto> feedbackTypesDtos) {

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

    private FeedbackTypesPagedResponse buildPagedFeedbackTypesResponse(
            String message,
            List<FeedbackTypesDto> feedbackTypesDtos,
            Integer totalPages,
            Long totalElements){

        FeedbackTypesPagedResponse.Data data = FeedbackTypesPagedResponse.Data.builder()
                .feedbackTypes(feedbackTypesDtos)
                .build();

        return FeedbackTypesPagedResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message(message)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .data(data)
                .build();
    }
}
