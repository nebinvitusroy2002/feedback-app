package flycatch.feedback.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @lombok.Data
    @Builder
    public static class Data{
        private Long id;
        private String name;
        private String email;
    }
}
