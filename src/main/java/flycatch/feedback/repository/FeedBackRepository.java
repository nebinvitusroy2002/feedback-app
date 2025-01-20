package flycatch.feedback.repository;

import flycatch.feedback.model.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedBackRepository extends JpaRepository<FeedBack,Long>, JpaSpecificationExecutor<FeedBack> {
}
