package flycatch.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFeedbackTypeDto {
    @NotBlank(message = "feedbackType.name.missing")
    private String name;
}
