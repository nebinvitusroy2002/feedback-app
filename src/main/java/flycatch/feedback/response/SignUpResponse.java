package flycatch.feedback.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SignUpResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data {
        private long id;
        private String userName;
        private String email;
        private List<String> roles;
    }
}
