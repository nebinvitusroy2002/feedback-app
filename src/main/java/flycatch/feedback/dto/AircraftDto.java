package flycatch.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AircraftDto {
    long id;
    private String name;
    private String type;
}
