package flycatch.feedback.service.aircraft;

import flycatch.feedback.dto.AircraftDto;
import flycatch.feedback.model.Aircraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AircraftServiceInterface {
    Aircraft createAircraft(AircraftDto aircraftDto);
    Aircraft getAircraftById(Long id);
    Page<Aircraft> getAllAircrafts(String searchTerm, Pageable pageable);
    Aircraft updateAircraft(Long id, AircraftDto aircraftDto) ;
    void deleteAircraft(Long id);
}
