package flycatch.feedback.service.auth;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.model.User;

public interface AuthServiceInterface {
    void forgotPassword(String email);
    User registerUser(RegisterRequest request);
    User loginUser(LoginRequest request);
    void resetPassword(String token,String newPassword);
}
