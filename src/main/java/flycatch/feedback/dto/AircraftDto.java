package flycatch.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AircraftDto {
    long id;
    @NotBlank(message = "aircraft.create.error.name.missing")
    private String name;
    @NotBlank(message = "aircraft.create.error.type.missing")
    private String type;
}
