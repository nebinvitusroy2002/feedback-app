package flycatch.feedback.controller;

import flycatch.feedback.dto.AircraftDto;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.response.aircraft.AircraftPagedResponse;
import flycatch.feedback.response.aircraft.AircraftResponse;
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
                List.of(createdDto)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        Aircraft aircraft = aircraftService.getAircraftById(id);
        AircraftDto aircraftDto = convertToDto(aircraft);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircraft retrieved successfully",
                List.of(aircraftDto)
        ));
    }

    @GetMapping
    public ResponseEntity<AircraftPagedResponse> getAllAircrafts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Aircraft> aircraftPage = aircraftService.getAllAircrafts(name, type, PageRequest.of(page, size));

        List<AircraftDto> aircraftDtos = aircraftPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = aircraftDtos.isEmpty() ? "No aircrafts found." : "Aircrafts retrieved successfully.";

        return ResponseEntity.ok(buildPagedAircraftResponse(
                message,
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
                List.of(updatedDto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AircraftResponse> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                "Aircraft deleted successfully",
                null
        ));
    }

    private AircraftDto convertToDto(Aircraft aircraft) {
        return new AircraftDto(aircraft.getId(), aircraft.getName(), aircraft.getType());
    }

    private AircraftResponse buildAircraftResponse(
            HttpStatus status,
            String message,
            List<AircraftDto> aircraftDtos) {
        AircraftResponse.Data data = AircraftResponse.Data.builder()
                .aircrafts(aircraftDtos)
                .build();
        AircraftResponse.AircraftResponseBuilder responseBuilder = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(status.value())
                .status(true)
                .message(message)
                .data(data);
        return responseBuilder.build();
    }

    private AircraftPagedResponse buildPagedAircraftResponse(
            String message,
            List<AircraftDto> aircraftDtos,
            Integer totalPages,
            Long totalElements) {

        AircraftPagedResponse.Data data = AircraftPagedResponse.Data.builder()
                .aircrafts(aircraftDtos)
                .build();

        AircraftPagedResponse.AircraftPagedResponseBuilder responseBuilder = AircraftPagedResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message(message)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .data(data);
        return responseBuilder.build();
    }
}
