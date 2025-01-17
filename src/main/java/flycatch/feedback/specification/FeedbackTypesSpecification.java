package flycatch.feedback.specification;

import flycatch.feedback.model.FeedbackTypes;
import flycatch.feedback.search.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

@AllArgsConstructor
public class FeedbackTypesSpecification implements Specification<FeedbackTypes> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            @NonNull Root<FeedbackTypes> root,
            CriteriaQuery<?> query,
            @NonNull CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.equal(
                        builder.lower(root.get(criteria.getKey())),
                        criteria.getValue().toString().toLowerCase()
                );
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
