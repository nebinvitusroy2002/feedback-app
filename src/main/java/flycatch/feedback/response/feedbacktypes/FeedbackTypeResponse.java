package flycatch.feedback.response.feedbacktypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import flycatch.feedback.dto.FeedbackTypesDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackTypeResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private List<FeedbackTypesDto> feedbackTypes;
    }
}
