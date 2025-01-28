package flycatch.feedback.util;

import org.springframework.data.domain.Sort;

public class SortUtil {
    public static Sort getSort(String sortParam) {
        if (sortParam == null || sortParam.isEmpty()) {
            return Sort.unsorted();
        }

        String[] sortFields = sortParam.split(",");
        Sort sort = Sort.unsorted();

        for (int i = 0; i < sortFields.length; i += 2) {
            String field = sortFields[i];
            String direction = (i + 1 < sortFields.length) ? sortFields[i + 1] : "asc";

            Sort fieldSort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(field).descending()
                    : Sort.by(field).ascending();

            sort = sort.and(fieldSort);
        }

        return sort;
    }
}
