package flycatch.feedback.response.aircraft;

import flycatch.feedback.dto.AircraftDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AircraftResponse {
    private String timestamp;
    private int code;
    private boolean status;
    private String message;
    private Data data;

    @Builder
    @Getter
    @Setter
    public static class Data {
        private List<AircraftDto> aircrafts;
    }
}
