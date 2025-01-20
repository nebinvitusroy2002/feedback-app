package flycatch.feedback.specification;

import flycatch.feedback.model.FeedBack;
import flycatch.feedback.search.SearchCriteria;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;


@AllArgsConstructor
public class FeedbackSpecification implements Specification<FeedBack> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            @NonNull Root<FeedBack> root,
            CriteriaQuery<?> query,
            @NonNull CriteriaBuilder builder) {

        Path<?> path;
        if (criteria.getKey().contains(".")) {
            String[] keys = criteria.getKey().split("\\.");
            path = root.get(keys[0]); // Root level
            for (int i = 1; i < keys.length; i++) {
                path = path.get(keys[i]);
            }
        } else {
            path = root.get(criteria.getKey());
        }

        if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (criteria.getKey().equalsIgnoreCase("feedbackText")) {
                return builder.like(
                        builder.lower(path.as(String.class)),
                        "%" + criteria.getValue().toString().toLowerCase() + "%"
                );
            } else if (criteria.getKey().equalsIgnoreCase("feedbackType.id")) {
                return builder.equal(path, criteria.getValue());
            }
        }
        return null;
    }
}