package flycatch.feedback.controller;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.service.auth.AuthService;
import flycatch.feedback.service.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request){
        User registeredUser = authService.registerUser(request);
        Map<String,Object> response = createUserResponse(registeredUser);
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

    private Map<String,Object> createUserResponse(User user){
        Map<String,Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        response.put("Code", HttpStatus.OK.value());
        response.put("Status",true);
        response.put("message","Signed up successfully");

        Map<String,Object> data = new LinkedHashMap<>();
        data.put("id",user.getId());
        data.put("name",user.getUserName());
        data.put("email",user.getEmail());

        response.put("data",data);
        return response;
    }
}
