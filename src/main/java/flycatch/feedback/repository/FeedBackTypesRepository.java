package flycatch.feedback.repository;

import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedBackTypesRepository extends JpaRepository<FeedbackTypes,Long> {
    Optional<FeedbackTypes> findByName(String name);
}
