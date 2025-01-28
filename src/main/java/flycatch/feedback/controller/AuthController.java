package flycatch.feedback.controller;

import flycatch.feedback.dto.*;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.response.Response;
import flycatch.feedback.response.SignUpResponse;
import flycatch.feedback.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> registerUser(@RequestBody RegisterRequest request, Locale locale) {
        SignUpResponse userResponse = authService.registerUser(request);
        userResponse.setMessage(messageSource.getMessage("user.signup.success", null, locale));
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody @Valid LoginRequest request, Locale locale) {
        LoginResponse loginResponse = authService.loginUser(request);
        return ResponseEntity.ok(createUserResponse(loginResponse, locale));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email, Locale locale) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(messageSource.getMessage("password.reset.link.sent", null, locale));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam("token") String token,
            @RequestBody ChangePasswordRequest request,
            Locale locale) {
        authService.changePassword(token, request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok(messageSource.getMessage("password.reset.success", null, locale));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request,
            Authentication authentication,
            Locale locale) {
        authService.resetPassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(messageSource.getMessage("password.update.success", null, locale));
    }

    private Response createUserResponse(LoginResponse loginResponse, Locale locale) {
        return Response.builder()
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .code(HttpStatus.OK.value())
                .status(true)
                .message(messageSource.getMessage("user.login.success", null, locale))
                .data(Response.Data.builder()
                        .email(loginResponse.getEmail())
                        .roles(loginResponse.getRoles())
                        .token(loginResponse.getToken())
                        .expiresIn(loginResponse.getExpiresIn())
                        .build())
                .build();
    }
}
