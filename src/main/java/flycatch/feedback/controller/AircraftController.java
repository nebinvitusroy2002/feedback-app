package flycatch.feedback.controller;

import flycatch.feedback.dto.AircraftDto;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.response.AircraftResponse;
import flycatch.feedback.service.aircraft.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aircrafts")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(@RequestBody AircraftDto aircraftDto) {
        Aircraft aircraft = aircraftService.createAircraft(aircraftDto);
        AircraftDto createdDto = convertToDto(aircraft);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildAircraftResponse(
                HttpStatus.CREATED,
                "Aircraft created successfully",
                List.of(createdDto),
                null,
                null
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        Aircraft aircraft = aircraftService.getAircraftById(id);
        AircraftDto aircraftDto = convertToDto(aircraft);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircraft retrieved successfully",
                List.of(aircraftDto),
                null,
                null
        ));
    }

    @GetMapping
    public ResponseEntity<AircraftResponse> getAllAircrafts(
            @RequestParam(required = false) String name, // Optional search term for name
            @RequestParam(required = false) String type, // Optional search term for type
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Aircraft> aircraftPage = aircraftService.getAllAircrafts(name, type, PageRequest.of(page, size));

        List<AircraftDto> aircraftDtos = aircraftPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircrafts retrieved successfully.",
                aircraftDtos,
                aircraftPage.getTotalPages(),
                aircraftPage.getTotalElements()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id, @RequestBody AircraftDto aircraftDto) {

        Aircraft aircraft = aircraftService.updateAircraft(id, aircraftDto);
        AircraftDto updatedDto = convertToDto(aircraft);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircraft updated successfully",
                List.of(updatedDto),
                null,
                null
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AircraftResponse> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircraft deleted successfully",
                null,
                null,
                null
        ));
    }

    private AircraftDto convertToDto(Aircraft aircraft) {
        return new AircraftDto(aircraft.getId(), aircraft.getName(), aircraft.getType());
    }

    private AircraftResponse buildAircraftResponse(
            HttpStatus status,
            String message,
            List<AircraftDto> aircraftDtos,
            Integer totalPages,
            Long totalElements) {

        AircraftResponse.Data data = AircraftResponse.Data.builder()
                .aircrafts(aircraftDtos)
                .build();

        return AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message)
                .data(data)
                .totalPages(totalPages != null ? totalPages : 0)
                .totalElements(totalElements != null ? totalElements : 0L)
                .build();
    }
}
