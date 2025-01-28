package flycatch.feedback.repository;

import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedBackTypesRepository extends JpaRepository<FeedbackTypes,Long>, JpaSpecificationExecutor<FeedbackTypes> {
    Page<FeedbackTypes> findByName(String name, Pageable pageable);
    @Query("SELECT COUNT(f) > 0 FROM FeedBack f WHERE f.feedbackType.id = :feedbackTypeId")
    boolean existsFeedbackForFeedbackType(@Param("feedbackTypeId") Long feedbackTypeId);
}
