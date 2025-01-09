package flycatch.feedback.service.userDetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsInterface {
    UserDetails loadUserByUsername(String email);
}
