package flycatch.feedback.controller;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.dto.ChangePasswordRequest;
import flycatch.feedback.dto.ResetPasswordRequest;
import flycatch.feedback.model.User;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.response.Response;
import flycatch.feedback.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = authService.loginUser(request);
        return ResponseEntity.ok(createUserResponse(loginResponse));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam("token") String token,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(token, request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request,
            Authentication authentication) {
        authService.resetPassword(authentication.getName(), request.getOldPassword(),request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully.");
    }


    private Response createUserResponse(LoginResponse loginResponse) {
        return Response.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Login successful")
                .data(Response.Data.builder()
                        .email(loginResponse.getEmail())
                        .roles(loginResponse.getRoles())
                        .token(loginResponse.getToken())
                        .expiresIn(loginResponse.getExpiresIn())
                        .build())
                .build();
    }
}
