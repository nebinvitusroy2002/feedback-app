package flycatch.feedback.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackTypesDto {
    private long id;
    private String name;
    private List<EmailDto> emails = new ArrayList<>();
}
