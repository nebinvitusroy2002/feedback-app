package flycatch.feedback.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
