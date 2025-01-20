package flycatch.feedback.dto;

import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackTypesDto {
    private long id;
    private String name;
}
