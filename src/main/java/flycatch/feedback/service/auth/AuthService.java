package flycatch.feedback.service.auth;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.exception.ResourceNotFoundException;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.repository.RoleRepository;
import flycatch.feedback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User registerUser(RegisterRequest request){

        log.info("Attempting to register user with email: {}",request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.error("User already registered with email: {}", request.getEmail());
            throw new ResourceNotFoundException("User already registered with email");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName("USER").orElseThrow(()->
                new ResourceNotFoundException("Default USER role not found in the database"));
        user.setRoles(List.of(userRole));

        try {
            User savedUser = userRepository.save(user);
            log.info("User successfully registered with email: {}", savedUser.getEmail());
            return savedUser;
        } catch (Exception e) {
            log.error("Error occurred while registering user with email: {}", request.getEmail(), e);
            throw new ResourceNotFoundException("Error occurred while registering the user");
        }
    }

    public User loginUser(LoginRequest request){
        log.info("Attempting to login with email: {}",request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User authenticatedUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("The request is invalid.Please check your input"));
            log.info("User successfully authenticated with email: {}",authenticatedUser.getEmail());
            return authenticatedUser;
        } catch (Exception e) {
            log.error("Authentication failed for user with email: {}", request.getEmail(), e);
            throw new ResourceNotFoundException("The request is invalid.Please check your input");
        }
    }
}
