package flycatch.feedback.service.email;

import java.util.List;

public interface EmailServiceInterface {
    void sendEmail(String to,String subject,String body);
    void sendNotification(String feedbackText, List<String> emails);
}
