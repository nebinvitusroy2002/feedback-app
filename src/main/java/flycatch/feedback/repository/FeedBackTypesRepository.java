package flycatch.feedback.repository;

import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedBackTypesRepository extends JpaRepository<FeedbackTypes,Long> {
    Page<FeedbackTypes> findByName(String name, Pageable pageable);
}
