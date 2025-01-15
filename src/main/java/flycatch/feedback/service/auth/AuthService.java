package flycatch.feedback.service.auth;

import flycatch.feedback.dto.LoginRequest;
import flycatch.feedback.dto.RegisterRequest;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.repository.RoleRepository;
import flycatch.feedback.repository.UserRepository;
import flycatch.feedback.response.LoginResponse;
import flycatch.feedback.service.email.EmailService;
import flycatch.feedback.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final JwtService jwtService;


    public User registerUser(RegisterRequest request){

        log.info("Attempting to register user with email: {}",request.getEmail());

        if (doesUserExistByEmail(request.getEmail())) {
            log.error("User already registered with email: {}", request.getEmail());
            throw new AppException("User already registered...");
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
            throw new AppException("Error occurred while registering...");
        }
    }

    public LoginResponse loginUser(LoginRequest request) {
        log.info("Attempting to login with email: {}", request.getEmail());


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User authenticatedUser = findUserByEmail(request.getEmail());
            log.info("User successfully authenticated with email: {}",authenticatedUser.getEmail());
            List<String> roles = authenticatedUser.getRoles().stream()
                    .map(Role::getName)
                    .toList();
            String token = jwtService.generateToken(authenticatedUser.getEmail(), roles);
            long expiresIn = jwtService.getExpirationTime();
            return LoginResponse.builder()
                    .token(token)
                    .expiresIn(expiresIn)
                    .email(authenticatedUser.getEmail())
                    .roles(roles)
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed for user with email: {}", request.getEmail(), e);
            throw new AppException("Username or Password incorrect");
        }
    }

    public void forgotPassword(String email){
        log.info("Processing forgot password for email: {}",email);

        User user;
        try {
                user = findUserByEmail(email);
            }catch (RuntimeException  e) {
                log.error("User with email {} not found: {}", email, e.getMessage());
                throw new AppException("User with the provided email does not exist...");
            }
            String token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:8080/auth/change-password?token="+token;
            emailService.sendEmail(
                    user.getEmail(),
                    "Password Reset Request",
                    "Click the link below to rest your password:\n"+resetLink
            );
            log.info("Password rest email sent to: {}",email);
    }

    public void changePassword(String token, String newPassword, String confirmPassword){
        log.info("Processing password reset with token: {}",token);

        if (!newPassword.equals(confirmPassword)) {
            log.error("Password and confirm password do not match");
            throw new AppException("Password and confirmation do not match.");
        }

        try {
            User user = findUserByResetToken(token);
            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())){
                log.error("Reset token expired for user: {}",user.getEmail());
                throw new AppException("Reset token has expired");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);

            log.info("Password successfully reset for user: {}",user.getEmail());
        }catch (NoSuchElementException  e) {
            log.error("Reset token error: {}", e.getMessage());
            throw new AppException("Invalid reset token.");
        } catch (Exception e) {
            log.error("Unexpected error during password reset for token {}: {}", token, e.getMessage());
            throw new AppException("An unexpected error occurred while resetting the password.");
        }
    }

    public void resetPassword(String email, String oldPassword, String newPassword) {
        log.info("Processing password change for user with email: {}", email);

        try {
            User user = findUserByEmail(email);
            if (user == null) {
                log.error("User not found with email: {}", email);
                throw new AppException("User not found...");
            }
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                log.error("Old password is incorrect for user: {}", email);
                throw new IllegalArgumentException("Old password is incorrect.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Password updated successfully for user: {}", email);
        }catch (RuntimeException e) {
            log.error("No user found with email: {}", email, e);
            throw new AppException("User not found.");
        } catch (Exception e) {
            log.error("Unexpected error during password reset for user {}: {}", email, e.getMessage());
            throw new AppException("An unexpected error occurred while resetting the password.");
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
            throw new AppException("Unable to verify if the user exists due to a database error.");
        }
    }


    private User findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        String errorMessage = "User not found with email: " + email;
                        log.error(errorMessage);
                        return new AppException(errorMessage);
                    });
        } catch (NoSuchElementException ex) {
            log.error("Resource not found exception occurred: {}", ex.getMessage());
            throw new AppException("Resource not found.");
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding user by email: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while processing the request");
        }
    }

    private User findUserByResetToken(String token) {
        try {
            return userRepository.findByResetToken(token)
                    .orElseThrow(() -> {
                        String errorMessage = "Invalid or expired reset token";
                        log.error(errorMessage);
                        return new AppException(errorMessage);
                    });
        } catch (NoSuchElementException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            throw new AppException("Resource not found.");
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding user by reset token: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while processing the request");
        }
    }


    private Role findRoleByName(String roleName) {
        try {
            return roleRepository.findByName(roleName)
                    .orElseThrow(() -> {
                        String errorMessage = "Role not found with name: " + roleName;
                        log.error(errorMessage);
                        return new AppException(errorMessage);
                    });
        } catch (NoSuchElementException ex) {
            log.error("Role not found: {}", ex.getMessage());
            throw new AppException("Role not found.");
        } catch (Exception ex) {
            log.error("Unexpected error occurred while finding role by name: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while processing the request");
        }
    }

}
