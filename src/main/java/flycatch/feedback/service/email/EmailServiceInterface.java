package flycatch.feedback.service.email;

public interface EmailServiceInterface {
    void sendEmail(String to,String subject,String body);
}
