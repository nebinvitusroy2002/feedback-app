package flycatch.feedback.service.auth;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.dto.UserDto;
import flycatch.feedback.model.User;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.response.SignUpResponse;

public interface AuthServiceInterface {
    void forgotPassword(String email);
    SignUpResponse registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
    void changePassword(String token, String newPassword, String confirmPassword);
    void resetPassword(String email, String oldPassword, String newPassword);
}
