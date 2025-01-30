package flycatch.feedback.response.feedbacks;

import com.fasterxml.jackson.annotation.JsonInclude;
import flycatch.feedback.dto.FeedBackDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedBackResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @Builder
    @Getter
    @Setter
    public static class Data{
        private List<FeedBackDto> feedbacks;
    }
}
