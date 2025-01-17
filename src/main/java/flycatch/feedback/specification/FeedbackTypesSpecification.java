package flycatch.feedback.specification;

import flycatch.feedback.model.FeedbackTypes;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class FeedbackTypesSpecification {
    public static Specification<FeedbackTypes> hasNameEqual(String search) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), search);
    }
}
