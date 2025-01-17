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

        AircraftResponse response = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.CREATED.value())
                .status(true)
                .message("Aircraft created successfully")
                .data(AircraftResponse.Data.builder()
                        .aircrafts(List.of(createdDto))
                        .build())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {

        Aircraft aircraft = aircraftService.getAircraftById(id);
        AircraftDto aircraftDto = convertToDto(aircraft);

        AircraftResponse response = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Aircraft retrieved successfully")
                .data(AircraftResponse.Data.builder()
                        .aircrafts(List.of(aircraftDto))
                        .build())
                .build();

        return ResponseEntity.ok(response);
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

        AircraftResponse response = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Aircrafts retrieved successfully.")
                .data(AircraftResponse.Data.builder()
                        .aircrafts(aircraftDtos)
                        .totalPages(aircraftPage.getTotalPages())
                        .totalElements(aircraftPage.getTotalElements())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id, @RequestBody AircraftDto aircraftDto) {

        Aircraft aircraft = aircraftService.updateAircraft(id, aircraftDto);
        AircraftDto updatedDto = convertToDto(aircraft);

        AircraftResponse response = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Aircraft updated successfully")
                .data(AircraftResponse.Data.builder()
                        .aircrafts(List.of(updatedDto))
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AircraftResponse> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);

        AircraftResponse response = AircraftResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .code(HttpStatus.OK.value())
                .status(true)
                .message("Aircraft deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    private AircraftDto convertToDto(Aircraft aircraft) {
        return new AircraftDto(aircraft.getId(), aircraft.getName(), aircraft.getType());
    }
}
