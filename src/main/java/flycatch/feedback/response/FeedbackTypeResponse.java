package flycatch.feedback.response;

import flycatch.feedback.dto.FeedbackTypesDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedbackTypeResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private String message;
        private List<FeedbackTypesDto> feedbackTypes;
        private FeedbackTypesDto feedbackType;
    }
}
