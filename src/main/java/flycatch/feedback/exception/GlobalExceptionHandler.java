package flycatch.feedback.exception;

import flycatch.feedback.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e, Locale locale) {
        log.error("AppException: {}", e.getMessage());

        String message = messageSource.getMessage(e.getMessage(), null, locale);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .success(false)
                        .message(message)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        log.error("Validation error: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error ->{
                    String defaultMessage = error.getDefaultMessage();
                    if (defaultMessage == null || defaultMessage.trim().isEmpty()) {
                        return "Validation failed. Please check the errors.";
                    }
                    return messageSource.getMessage(defaultMessage, null, locale);
                })
                .orElse("Validation failed. Please check the errors.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .success(false)
                        .message(errorMessage)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, Locale locale) {
        log.error("Unhandled exception: {}", e.getMessage(), e);

        String message = messageSource.getMessage("error.unexpected", null, locale);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .success(false)
                        .message(message)
                        .build());
    }
}
