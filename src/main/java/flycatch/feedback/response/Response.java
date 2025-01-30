package flycatch.feedback.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
