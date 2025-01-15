package flycatch.feedback.controller;

import flycatch.feedback.dto.FeedbackTypesDto;
import flycatch.feedback.response.FeedbackTypeResponse;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback-types")
@RequiredArgsConstructor
public class FeedbackTypesController {

    private final FeedbackTypesService feedbackTypesService;

    @PostMapping
    public ResponseEntity<FeedbackTypeResponse> createFeedbackType(@RequestBody FeedbackTypesDto feedbackTypesDto) {
        FeedbackTypeResponse response = feedbackTypesService.createFeedbackType(feedbackTypesDto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> getFeedbackTypeById(@PathVariable long id) {
        FeedbackTypeResponse response = feedbackTypesService.getFeedbackTypeById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<FeedbackTypeResponse> getAllFeedbackTypes() {
        FeedbackTypeResponse response = feedbackTypesService.getAllFeedbackTypes();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> updateFeedbackType(
            @PathVariable long id,
            @RequestBody FeedbackTypesDto feedbackTypesDto) {
        FeedbackTypeResponse response = feedbackTypesService.updateFeedbackType(id, feedbackTypesDto);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> deleteFeedbackType(@PathVariable long id) {
        FeedbackTypeResponse response = feedbackTypesService.deleteFeedbackType(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
