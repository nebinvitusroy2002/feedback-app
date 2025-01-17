package flycatch.feedback.service.aircraft;

import flycatch.feedback.dto.AircraftDto;
import flycatch.feedback.exception.AppException;
import flycatch.feedback.model.Aircraft;
import flycatch.feedback.repository.AircraftRepository;
import flycatch.feedback.search.SearchCriteria;
import flycatch.feedback.specification.AircraftSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AircraftService implements AircraftServiceInterface{

    private final AircraftRepository aircraftRepository;

    public Aircraft createAircraft(AircraftDto aircraftDto){
        log.info("Creating a new aircraft: {}",aircraftDto.getName());
        Aircraft aircraft = new Aircraft();
        aircraft.setName(aircraftDto.getName());
        aircraft.setType(aircraftDto.getType());
        try {
            return aircraftRepository.save(aircraft);
        }catch (Exception ex) {
            log.error("Unexpected error while creating new aircraft: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while creating new aircraft. Please try again later.");
        }
    }

    public Aircraft getAircraftById(Long id) {
        log.info("Fetching aircraft with id: {}", id);
        try {
            return aircraftRepository.findById(id)
                    .orElseThrow(() -> new AppException("Aircraft not found with id: " + id));
        }catch (Exception ex) {
            log.error("Unexpected error while fetching aircrafts: {}", ex.getMessage());
            throw new AppException("An unexpected error occurred while fetching aircrafts. Please try again later.");
        }
    }

    public Page<Aircraft> getAllAircrafts(String name, String type, Pageable pageable) {
        Specification<Aircraft> specification = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            SearchCriteria nameCriteria = new SearchCriteria("name", ":", name);
            specification = specification.and(new AircraftSpecification(nameCriteria));
        }

        if (type != null && !type.isEmpty()) {
            SearchCriteria typeCriteria = new SearchCriteria("type", ":", type);
            specification = specification.and(new AircraftSpecification(typeCriteria));
        }

        try {
            return aircraftRepository.findAll(specification, pageable);
        } catch (Exception ex) {
            log.error("Error while searching aircrafts: {}", ex.getMessage());
            throw new AppException("Unable to search aircrafts. Please try again later.");
        }
    }

    public Aircraft updateAircraft(Long id, AircraftDto aircraftDto) {
        log.info("Updating aircraft with id: {}", id);
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AppException("Aircraft not found with id: " + id));
        aircraft.setName(aircraftDto.getName());
        aircraft.setType(aircraftDto.getType());
        try {
            return aircraftRepository.save(aircraft);
        }catch (Exception ex) {
            log.error("Error while updating aircrafts: {}", ex.getMessage());
            throw new AppException("Unable to update aircrafts. Please try again later.");
        }
    }

    public void deleteAircraft(Long id) {
        log.info("Deleting aircraft with id: {}", id);
        aircraftRepository.findById(id)
                .orElseThrow(() -> new AppException("Aircraft not found with id: " + id));
        aircraftRepository.deleteById(id);
    }
}
