package flycatch.feedback.controller;

import flycatch.feedback.dto.*;
import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.response.feedbacktypes.FeedbackTypeResponse;
import flycatch.feedback.response.feedbacktypes.FeedbackTypesPagedResponse;
import flycatch.feedback.service.feedBackTypes.FeedbackTypesService;
import flycatch.feedback.util.SortUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback-types")
@RequiredArgsConstructor
public class FeedbackTypesController {

    private final FeedbackTypesService feedbackTypesService;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<FeedbackTypeResponse> createFeedbackType(@Valid @RequestBody CreateFeedbackTypeDto createFeedbackTypeDto) {
        FeedbackTypes feedbackType = feedbackTypesService.createFeedbackType(createFeedbackTypeDto);
        FeedbackTypesDto createdDto = convertToDto(feedbackType);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildFeedbackTypeResponse(
                HttpStatus.CREATED,
                messageSource.getMessage("feedback.type.created",null, Locale.getDefault()),
                List.of(createdDto)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> getFeedbackTypeById(@PathVariable long id) {
        FeedbackTypes feedbackType = feedbackTypesService.getFeedbackTypeById(id);
        FeedbackTypesDto feedbackTypeDto = convertToDto(feedbackType);

        return ResponseEntity.ok(buildFeedbackTypeResponse(
                HttpStatus.OK,
                messageSource.getMessage("feedback.type.retrieved",null,Locale.getDefault()),
                List.of(feedbackTypeDto)
        ));
    }

    @GetMapping
    public ResponseEntity<FeedbackTypesPagedResponse> getAllFeedbackTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Page<FeedbackTypes> feedbackTypesPage = feedbackTypesService.getAllFeedbackTypes(
                name,
                PageRequest.of(page, size, SortUtil.getSort(sort))
        );

        List<FeedbackTypesDto> feedbackTypesDtos = feedbackTypesPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = feedbackTypesDtos.isEmpty()
                ? messageSource.getMessage("feedback.type.empty.list", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("feedback.type.retrieved", null, LocaleContextHolder.getLocale());

        return ResponseEntity.ok(buildPagedFeedbackTypesResponse(
                message,
                feedbackTypesDtos,
                feedbackTypesPage.getTotalPages(),
                feedbackTypesPage.getTotalElements()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> updateFeedbackType(
            @PathVariable long id,
            @RequestBody UpdateFeedbackTypesDto updateFeedbackTypesDto) {

        FeedbackTypes feedbackType = feedbackTypesService.updateFeedbackType(id, updateFeedbackTypesDto);
        FeedbackTypesDto updatedDto = convertToDto(feedbackType);

        return ResponseEntity.ok(buildFeedbackTypeResponse(
                HttpStatus.OK,
                messageSource.getMessage("feedback.type.updated",null,Locale.getDefault()),
                List.of(updatedDto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FeedbackTypeResponse> deleteFeedbackType(@PathVariable long id) {
        feedbackTypesService.deleteFeedbackType(id);

        return ResponseEntity.ok(buildFeedbackTypeResponse(
                HttpStatus.OK,
                messageSource.getMessage("feedback.type.deleted",null,Locale.getDefault()),
                null
        ));
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

        FeedbackTypeResponse.FeedbackTypeResponseBuilder responseBuilder = FeedbackTypeResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message);

        if (feedbackTypesDtos != null){
            FeedbackTypeResponse.Data data = FeedbackTypeResponse.Data.builder()
                    .feedbackTypes(feedbackTypesDtos)
                    .build();
            responseBuilder.data(data);
        }
        return responseBuilder.build();
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
