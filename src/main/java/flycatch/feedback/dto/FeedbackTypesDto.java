package flycatch.feedback.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackTypesDto {
    private Long id;
    private String name;
    private List<EmailDto> emails;
}
