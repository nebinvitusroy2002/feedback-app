package flycatch.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportGenerationDto {
    private Long id;
    private String feedbackText;
    private String feedbackType;
    private String aircraftName;
    private String aircraftType;
}
