package flycatch.feedback.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception ex) {
            log.error("Failed to send email to: {}. Error: {}", to, ex.getMessage());
        }
    }

    public void sendNotification(String feedbackText, List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            log.warn("No email recipients found for feedback notification.");
            return;
        }

        log.info("Sending notification to {} recipients. Feedback: {}", emails.size(), feedbackText);
        for (String email : emails) {
            sendEmail(email, "New Feedback Notification", "New feedback received: " + feedbackText);
        }
        log.info("Notification emails sent to all recipients.");
    }
}
