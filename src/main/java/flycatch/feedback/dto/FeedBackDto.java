package flycatch.feedback.dto;

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
    private String feedbackText;
    private long feedbackTypeId;
    private long aircraftId;
}
