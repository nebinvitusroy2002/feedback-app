package flycatch.feedback.datainit;

import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.model.Role;
import flycatch.feedback.model.User;
import flycatch.feedback.repository.FeedBackTypesRepository;
import flycatch.feedback.repository.RoleRepository;
import flycatch.feedback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeedBackTypesRepository feedBackTypesRepository;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Bootstrapping roles and admin user...");

        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setUserName("ADMIN");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            List<Role> roles = new ArrayList<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            log.info("Admin user created with email: admin@example.com");
        } else {
            log.info("Admin user already exists.");
        }

        List<String> feedbackTypeNames = Arrays.asList(
                "Commissary",
                "Catering",
                "FBO/VIP Representative",
                "Aircraft Cleaning",
                "Flight Attendance Performance"
        );

        feedbackTypeNames.forEach(typeName -> {
            Pageable pageable = PageRequest.of(0, 1);
            Page<FeedbackTypes> feedbackTypesPage = feedBackTypesRepository.findByName(typeName, pageable);
            if (feedbackTypesPage.isEmpty()) {
                FeedbackTypes feedbackTypes = new FeedbackTypes();
                feedbackTypes.setName(typeName);
                feedBackTypesRepository.save(feedbackTypes);
                log.info("Feedback type '{}' created.", typeName);
            } else {
                log.info("Feedback type '{}' already exists.", typeName);
            }
        });
    }
}
