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
public class AuthService implements AuthServiceInterface{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User registerUser(RegisterRequest request){

        log.info("Attempting to register user with email: {}",request.getEmail());

        if (doesUserExistByEmail(request.getEmail())) {
            log.error("User already registered with email: {}", request.getEmail());
            throw new ResourceNotFoundException("User already registered with email");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = findRoleByName("USER");
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
            User authenticatedUser = findUserByEmail(request.getEmail());
            log.info("User successfully authenticated with email: {}",authenticatedUser.getEmail());
            return authenticatedUser;
        } catch (Exception e) {
            log.error("Authentication failed for user with email: {}", request.getEmail(), e);
            throw new ResourceNotFoundException("The request is invalid.Please check your input");
        }
    }

    private boolean doesUserExistByEmail(String email) {
        try {
            boolean exists = userRepository.findByEmail(email).isPresent();
            if (exists) {
                log.info("User with email {} already exists in the database.", email);
            } else {
                log.info("User with email {} does not exist in the database.", email);
            }
            return exists;
        } catch (Exception e) {
            log.error("An error occurred while checking if the user with email {} exists in the database.", email, e);
            throw new ResourceNotFoundException("Unable to verify if the user exists due to a database error.");
        }
    }


    private User findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        String errorMessage = "User not found with email: " + email;
                        log.error(errorMessage);
                        return new ResourceNotFoundException(errorMessage);
                    });
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found exception occurred: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding user by email: {}", ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing the request", ex);
        }
    }


    private Role findRoleByName(String roleName) {
        try {
            return roleRepository.findByName(roleName)
                    .orElseThrow(() -> {
                        String errorMessage = "Role not found with name: " + roleName;
                        log.error(errorMessage);
                        return new ResourceNotFoundException(errorMessage);
                    });
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding role by name: {}", ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing the request", ex);
        }
    }

}
