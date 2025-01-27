package flycatch.feedback.controller;

import flycatch.feedback.dto.AircraftDto;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.response.aircraft.AircraftPagedResponse;
import flycatch.feedback.response.aircraft.AircraftResponse;
import flycatch.feedback.service.aircraft.AircraftService;
import flycatch.feedback.util.SortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aircrafts")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(@RequestBody AircraftDto aircraftDto) {
        Aircraft aircraft = aircraftService.createAircraft(aircraftDto);
        AircraftDto createdDto = convertToDto(aircraft);

        return ResponseEntity.status(HttpStatus.CREATED).body(buildAircraftResponse(
                HttpStatus.CREATED,
                messageSource.getMessage("aircraft.create.success", null, Locale.getDefault()),
                List.of(createdDto)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        Aircraft aircraft = aircraftService.getAircraftById(id);
        AircraftDto aircraftDto = convertToDto(aircraft);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                messageSource.getMessage("aircraft.fetch.success", null, Locale.getDefault()),
                List.of(aircraftDto)
        ));
    }

    @GetMapping
    public ResponseEntity<AircraftPagedResponse> getAllAircrafts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        Page<Aircraft> aircraftPage = aircraftService.getAllAircrafts(
                name,
                type,
                PageRequest.of(page, size, SortUtil.getSort(sort))
        );

        List<AircraftDto> aircraftDtos = aircraftPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        String message = aircraftDtos.isEmpty()
                ? messageSource.getMessage("aircraft.fetch.notfound", null, Locale.getDefault())
                : messageSource.getMessage("aircraft.fetch.success", null, Locale.getDefault());

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
                messageSource.getMessage("aircraft.update.success", null, Locale.getDefault()),
                List.of(updatedDto)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AircraftResponse> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);

        return ResponseEntity.ok(buildAircraftResponse(
                HttpStatus.OK,
                messageSource.getMessage("aircraft.delete.success", null, Locale.getDefault()),
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
