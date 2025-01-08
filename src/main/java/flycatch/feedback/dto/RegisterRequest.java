package flycatch.feedback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name cannot be blank")
    private String userName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4,message = "Password must be at least 4 Characters long")
    private String password;
}
