package flycatch.feedback.repository;

import flycatch.feedback.model.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AircraftRepository extends JpaRepository<Aircraft,Long>, JpaSpecificationExecutor<Aircraft> {
}
