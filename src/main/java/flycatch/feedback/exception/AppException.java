package flycatch.feedback.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AppException extends RuntimeException{
   private final String message;
}
