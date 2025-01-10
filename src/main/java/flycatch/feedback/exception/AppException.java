package flycatch.feedback.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppException extends RuntimeException{
    public AppException(String message){
        super(message);
    }
}
