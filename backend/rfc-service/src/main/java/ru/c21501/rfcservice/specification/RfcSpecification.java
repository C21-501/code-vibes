package ru.c21501.rfcservice.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.util.UUID;

/**
 * Спецификации для фильтрации RFC
 */
public class RfcSpecification {

    /**
     * Фильтр по статусу
     */
    public static Specification<Rfc> hasStatus(RfcStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Фильтр по приоритету
     */
    public static Specification<Rfc> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) -> {
            if (priority == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("priority"), priority);
        };
    }

    /**
     * Фильтр по ID создателя
     */
    public static Specification<Rfc> hasCreatedById(UUID createdById) {
        return (root, query, criteriaBuilder) -> {
            if (createdById == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("createdBy").get("id"), createdById);
        };
    }

    /**
     * Фильтр по заголовку (содержит подстроку)
     */
    public static Specification<Rfc> titleContains(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }
}
