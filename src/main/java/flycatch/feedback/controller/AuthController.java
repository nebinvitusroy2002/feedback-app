package flycatch.feedback.controller;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.dto.ResetPasswordRequest;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.response.Response;
import flycatch.feedback.service.auth.AuthService;
import flycatch.feedback.service.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<Response> register(@RequestBody @Valid RegisterRequest request){
        User registeredUser = authService.registerUser(request);
        Response response = createUserResponse(registeredUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginRequest request){
        User authenticatedUser = authService.loginUser(request);
        List<String> roles = authenticatedUser
                .getRoles()
                .stream()
                .map(Role::getName)
                .toList();
        String jwtToken = jwtService.generateToken(authenticatedUser.getEmail(),roles);
        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestBody ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }
        authService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully.");
    }

    private Response createUserResponse(User user){
        return Response.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Signed Up successfully")
                .data(Response.Data.builder()
                        .id(user.getId())
                        .name(user.getUserName())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}
