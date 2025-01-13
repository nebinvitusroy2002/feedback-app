package flycatch.feedback.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
        private String email;
        private List<String> roles;
        private String token;
        private long expiresIn;
    }
}
