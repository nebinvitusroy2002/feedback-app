package flycatch.feedback.repository;

import flycatch.feedback.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AircraftRepository extends JpaRepository<Aircraft,Long>, JpaSpecificationExecutor<Aircraft> {
    @Query("SELECT COUNT(f) > 0 FROM FeedBack f WHERE f.aircraft.id = :aircraftId")
    boolean existsFeedbackForAircraft(@Param("aircraftId") Long aircraftId);
}
