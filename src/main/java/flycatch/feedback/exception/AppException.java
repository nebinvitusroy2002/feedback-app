package flycatch.feedback.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AppException extends RuntimeException{
   public AppException(String message) {
      super(message);
   }

   public AppException(String message, Object... args) {
      super(String.format(message, args));
   }
}
