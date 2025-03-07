package flycatch.feedback.response.feedbacks;

import flycatch.feedback.dto.FeedBackDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FeedbackPagedResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;
    private Integer totalPages;
    private Long totalElements;

    @Builder
    @Getter
    @Setter
    public static class Data{
        private List<FeedBackDto> feedbacks;
    }
}
