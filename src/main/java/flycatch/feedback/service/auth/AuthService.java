package flycatch.feedback.service.auth;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.exception.ResourceNotFoundException;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.repository.RoleRepository;
import flycatch.feedback.repository.UserRepository;
import flycatch.feedback.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

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

    public void forgotPassword(String email){
        log.info("Processing forgot password for email: {}",email);

        try {
            User user = findUserByEmail(email);
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);

            String resetLink = "http://localhost:8080/auth/reset-password?token="+token;
            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset Request",
                    "Click the link belo to rest your password:\n"+resetLink
            );
            log.info("Password rest email sent to: {}",email);
        }catch (ResourceNotFoundException e) {
            log.error("User with email {} not found: {}", email, e.getMessage());
            throw e; // Bubble up custom exception
        } catch (Exception e) {
            log.error("Unexpected error during forgot password process for email {}: {}", email, e.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing the forgot password request.", e);
        }
    }

    public void resetPassword(String token,String newPassword){
        log.info("Processing password reset with token: {}",token);

        try {
            User user = findUserByResetToken(token);
            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())){
                log.error("Reset token expired for user: {}",user.getEmail());
                throw new ResourceNotFoundException("Reset token has expired");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);

            log.info("Password successfully reset for user: {}",user.getEmail());
        }catch (ResourceNotFoundException e) {
            log.error("Reset token error: {}", e.getMessage());
            throw e; // Bubble up custom exception
        } catch (Exception e) {
            log.error("Unexpected error during password reset for token {}: {}", token, e.getMessage());
            throw new RuntimeException("An unexpected error occurred while resetting the password.", e);
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

    private User findUserByResetToken(String token) {
        try {
            return userRepository.findByResetToken(token)
                    .orElseThrow(() -> {
                        String errorMessage = "Invalid or expired reset token";
                        log.error(errorMessage);
                        return new ResourceNotFoundException(errorMessage);
                    });
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding user by reset token: {}", ex.getMessage());
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
            log.error("Role not found: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding role by name: {}", ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing the request", ex);
        }
    }

}
