package flycatch.feedback.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFeedbackTypesDto {
    private String name;
    private List<EmailDto> emails = new ArrayList<>();
}
