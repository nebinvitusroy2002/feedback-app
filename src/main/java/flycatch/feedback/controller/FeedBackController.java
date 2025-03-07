package flycatch.feedback.controller;

import flycatch.feedback.dto.FeedBackDto;
import flycatch.feedback.model.FeedBack;
import flycatch.feedback.response.feedbacks.FeedBackResponse;
import flycatch.feedback.response.feedbacks.FeedbackPagedResponse;
import flycatch.feedback.service.feedbacks.FeedBackService;
import flycatch.feedback.util.SortUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedBackController {

    private final FeedBackService feedBackService;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<FeedBackResponse> createFeedback(@Valid @RequestBody FeedBackDto feedBackDto) {
        FeedBack feedBack = feedBackService.createFeedback(feedBackDto);
        FeedBackDto createdDto = convertToDto(feedBack);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildFeedBackResponse(
                HttpStatus.CREATED,
                messageSource.getMessage("feedback.create.success", null, Locale.getDefault()),
                List.of(createdDto)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedBackResponse> getFeedbackById(@PathVariable Long id) {
        FeedBack feedBack = feedBackService.getFeedbackById(id);
        FeedBackDto feedBackDto = convertToDto(feedBack);

        return ResponseEntity.ok(buildFeedBackResponse(
                HttpStatus.OK,
                messageSource.getMessage("feedback.fetch.success", null, Locale.getDefault()),
                List.of(feedBackDto)
        ));
    }

    @GetMapping
    public ResponseEntity<FeedbackPagedResponse> getAllFeedbacks(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Page<FeedBack> feedbackPage = feedBackService.getAllFeedbacks(
                text,
                typeId,
                PageRequest.of(page, size, SortUtil.getSort(sort))
        );

        List<FeedBackDto> feedbackDtos = feedbackPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = feedbackDtos.isEmpty()
                ? messageSource.getMessage("feedback.fetch.notfound.success", null, Locale.getDefault())
                : messageSource.getMessage("feedback.fetch.success", null, Locale.getDefault());

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
                messageSource.getMessage("feedback.update.success", null, Locale.getDefault()),
                List.of(updatedDto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedBackResponse> deleteFeedback(@PathVariable Long id) {
        feedBackService.deleteByFeedbackId(id);

        return ResponseEntity.ok(buildFeedBackResponse(
                HttpStatus.OK,
                messageSource.getMessage("feedback.delete.success", null, Locale.getDefault()),
                null
        ));
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> exportFeedbackReport(
            @RequestParam(required = false) Long feedbackTypeId,
            @RequestParam(required = false) Long aircraftId) {

        byte[] report = feedBackService.exportFeedbackReport(feedbackTypeId, aircraftId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("feedback_report.xlsx").build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
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
            List<FeedBackDto> feedbackDtos) {

        FeedBackResponse.FeedBackResponseBuilder response = FeedBackResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message);

        if (feedbackDtos != null){
            FeedBackResponse.Data data = FeedBackResponse.Data.builder()
                    .feedbacks(feedbackDtos)
                    .build();
            response.data(data);
        }
        return response.build();
    }

    private FeedbackPagedResponse buildPagedFeedBackResponse(
            String message,
            List<FeedBackDto> feedbackDtos,
            Integer totalPages,
            Long totalElements) {

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
