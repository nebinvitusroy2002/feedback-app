package flycatch.feedback.repository;

import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedBackTypesRepository extends JpaRepository<FeedbackTypes,Long>, JpaSpecificationExecutor<FeedbackTypes> {
    Page<FeedbackTypes> findByName(String name, Pageable pageable);
}
