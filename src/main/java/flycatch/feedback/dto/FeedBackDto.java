package flycatch.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackDto {

    private long id;
    @NotBlank(message = "feedback.require.text")
    private String feedbackText;
    @NotNull(message = "feedback.require.type")
    private long feedbackTypeId;
    @NotNull(message = "feedback.require.aircraftId")
    private long aircraftId;
}
