package flycatch.feedback.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LoginResponse {

    private String token;
    private long expiresIn;
    private String email;
    private List<String> roles;


    public LoginResponse(String token, long expiresIn, String email, List<String> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.roles = roles;
    }
}
