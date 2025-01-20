package flycatch.feedback.specification;

import flycatch.feedback.model.Aircraft;
import flycatch.feedback.search.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

@AllArgsConstructor
public class AircraftSpecification implements Specification<Aircraft> {

    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            @NonNull Root<Aircraft> root,
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
