package flycatch.feedback.controller;

import flycatch.feedback.dto.FeedBackDto;
import flycatch.feedback.model.FeedBack;
import flycatch.feedback.response.feedbacks.FeedBackResponse;
import flycatch.feedback.response.feedbacks.FeedbackPagedResponse;
import flycatch.feedback.service.feedbacks.FeedBackService;
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
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<FeedBackResponse> createFeedback(@RequestBody FeedBackDto feedBackDto){
        FeedBack feedBack = feedBackService.createFeedback(feedBackDto);
        FeedBackDto createdDto = convertToDto(feedBack);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildFeedBackResponse(
                HttpStatus.CREATED,
                "Feedback created successfully",
                List.of(createdDto)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedBackResponse> getFeedbackById(@PathVariable Long id) {
        FeedBack feedBack = feedBackService.getFeedbackById(id);
        FeedBackDto feedBackDto = convertToDto(feedBack);

        return ResponseEntity.ok(buildFeedBackResponse(
                HttpStatus.OK,
                "Feedback retrieved successfully",
                List.of(feedBackDto)
        ));
    }

    @GetMapping
    public ResponseEntity<FeedbackPagedResponse> getAllFeedbacks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer feedbackType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<FeedBack> feedbackPage = feedBackService.getAllFeedbacks(search, feedbackType, PageRequest.of(page, size));

        List<FeedBackDto> feedbackDtos = feedbackPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = feedbackDtos.isEmpty() ? "No feedbacks found." : "Feedbacks retrieved successfully.";

        return ResponseEntity.ok(buildPagedFeedBackResponse(
                message,
                feedbackDtos,
                feedbackPage.getTotalPages(),
                feedbackPage.getTotalElements()
        ));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FeedBackResponse> updateFeedback(
            @PathVariable Long id, @RequestBody FeedBackDto feedBackDto) {

        FeedBack feedBack = feedBackService.updateFeedback(id, feedBackDto);
        FeedBackDto updatedDto = convertToDto(feedBack);

        return ResponseEntity.ok(buildFeedBackResponse(
                HttpStatus.OK,
                "Feedback updated successfully",
                List.of(updatedDto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedBackResponse> deleteFeedback(@PathVariable Long id) {
        feedBackService.deleteByAircraftId(id);

        return ResponseEntity.ok(buildFeedBackResponse(
                HttpStatus.OK,
                "Feedback deleted successfully",
                null
        ));
    }

    private FeedBackDto convertToDto(FeedBack feedBack) {
        return new FeedBackDto(
                feedBack.getId(),
                feedBack.getFeedbackText(),
                feedBack.getFeedbackType().getId(),
                feedBack.getAircraft().getId()
        );
    }

    private FeedBackResponse buildFeedBackResponse(
            HttpStatus status,
            String message,
            List<FeedBackDto> feedbackDtos){

        FeedBackResponse.Data data = FeedBackResponse.Data.builder()
                .feedbacks(feedbackDtos)
                .build();

        return FeedBackResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message)
                .data(data)
                .build();
    }

    private FeedbackPagedResponse buildPagedFeedBackResponse(
            String message,
            List<FeedBackDto> feedbackDtos,
            Integer totalPages,
            Long totalElements){

        FeedbackPagedResponse.Data data = FeedbackPagedResponse.Data.builder()
                .feedbacks(feedbackDtos)
                .build();

        return FeedbackPagedResponse.builder()
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
