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
public class AircraftService implements AircraftServiceInterface {

    private final AircraftRepository aircraftRepository;

    public Aircraft createAircraft(AircraftDto aircraftDto) {
        log.info("Creating a new aircraft: {}", aircraftDto.getName());
        Aircraft aircraft = new Aircraft();
        aircraft.setName(aircraftDto.getName());
        aircraft.setType(aircraftDto.getType());
        try {
            return aircraftRepository.save(aircraft);
        } catch (Exception ex) {
            log.error("Unexpected error while creating new aircraft: {}", ex.getMessage());
            throw new AppException("aircraft.create.error");
        }
    }

    public Aircraft getAircraftById(Long id) {
        log.info("Fetching aircraft with id: {}", id);
        try {
            return aircraftRepository.findById(id)
                    .orElseThrow(() -> new AppException("aircraft.fetch.notfound", String.valueOf(id)));
        } catch (Exception ex) {
            log.error("Unexpected error while fetching aircraft: {}", ex.getMessage());
            throw new AppException("aircraft.fetch.notfound");
        }
    }

    public Page<Aircraft> getAllAircrafts(String searchTerm, Pageable pageable) {
        Specification<Aircraft> specification = Specification.where(null);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            Specification<Aircraft> nameSpec = new AircraftSpecification(new SearchCriteria("name", ":", searchTerm));
            Specification<Aircraft> typeSpec = new AircraftSpecification(new SearchCriteria("type", ":", searchTerm));
            specification = specification.and(nameSpec.or(typeSpec));
        }

        try {
            return aircraftRepository.findAll(specification, pageable);
        } catch (Exception ex) {
            log.error("Error while searching aircrafts: {}", ex.getMessage());
            throw new AppException("aircraft.error");
        }
    }

    public Aircraft updateAircraft(Long id, AircraftDto aircraftDto) {
        log.info("Updating aircraft with id: {}", id);
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new AppException("aircraft.fetch.notfound",String.valueOf(id)));
        if (aircraftDto.getName() != null) {
            aircraft.setName(aircraftDto.getName());
        }
        if (aircraftDto.getType() != null) {
            aircraft.setType(aircraftDto.getType());
        }

        try {
            return aircraftRepository.save(aircraft);
        } catch (Exception ex) {
            log.error("Error while updating aircraft: {}", ex.getMessage());
            throw new AppException("aircraft.update.error");
        }
    }

    public void deleteAircraft(Long id) {
        log.info("Deleting aircraft with id: {}", id);
        if (aircraftRepository.existsFeedbackForAircraft(id)) {
            log.error("Cannot delete aircraft with id {}: Feedback exists.", id);
            throw new AppException("aircraft.delete.feedbackexists");
        }
        aircraftRepository.findById(id)
                .orElseThrow(() -> new AppException("aircraft.fetch.notfound",String.valueOf(id)));
        try {
            aircraftRepository.deleteById(id);
            log.info("Aircraft deleted successfully with id: {}", id);
        } catch (Exception ex) {
            log.error("Error while deleting aircraft with id {}: {}", id, ex.getMessage());
            throw new AppException("aircraft.delete.error");
        }
    }
}
