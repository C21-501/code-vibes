package ru.c21501.rfcservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.openapi.model.Urgency;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification для фильтрации RFC
 */
public class RfcSpecification {

    /**
     * Спецификация для исключения удаленных RFC
     *
     * @return спецификация
     */
    public static Specification<RfcEntity> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get("deletedDatetime"));
    }

    /**
     * Спецификация для фильтрации по статусу
     *
     * @param status статус RFC
     * @return спецификация
     */
    public static Specification<RfcEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            try {
                RfcStatus rfcStatus = RfcStatus.valueOf(status);
                return criteriaBuilder.equal(root.get("status"), rfcStatus);
            } catch (IllegalArgumentException e) {
                // Игнорируем невалидный статус
                return criteriaBuilder.conjunction();
            }
        };
    }

    /**
     * Спецификация для фильтрации по срочности
     *
     * @param urgency срочность RFC
     * @return спецификация
     */
    public static Specification<RfcEntity> hasUrgency(String urgency) {
        return (root, query, criteriaBuilder) -> {
            if (urgency == null || urgency.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            try {
                Urgency urgencyEnum = Urgency.valueOf(urgency);
                return criteriaBuilder.equal(root.get("urgency"), urgencyEnum);
            } catch (IllegalArgumentException e) {
                // Игнорируем невалидную срочность
                return criteriaBuilder.conjunction();
            }
        };
    }

    /**
     * Спецификация для фильтрации по ID создателя
     *
     * @param requesterId ID создателя
     * @return спецификация
     */
    public static Specification<RfcEntity> hasRequesterId(Long requesterId) {
        return (root, query, criteriaBuilder) -> {
            if (requesterId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("requester").get("id"), requesterId);
        };
    }

    /**
     * Создает спецификацию для фильтрации RFC по заданным параметрам
     * Все фильтры применяются через AND
     *
     * @param status      фильтр по статусу
     * @param urgency     фильтр по срочности
     * @param requesterId фильтр по ID создателя
     * @return спецификация для фильтрации
     */
    public static Specification<RfcEntity> withFilters(String status, String urgency, Long requesterId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Исключаем удаленные RFC
            predicates.add(criteriaBuilder.isNull(root.get("deletedDatetime")));

            // Фильтр по статусу
            if (status != null && !status.trim().isEmpty()) {
                try {
                    RfcStatus rfcStatus = RfcStatus.valueOf(status);
                    predicates.add(criteriaBuilder.equal(root.get("status"), rfcStatus));
                } catch (IllegalArgumentException e) {
                    // Игнорируем невалидный статус
                }
            }

            // Фильтр по срочности
            if (urgency != null && !urgency.trim().isEmpty()) {
                try {
                    Urgency urgencyEnum = Urgency.valueOf(urgency);
                    predicates.add(criteriaBuilder.equal(root.get("urgency"), urgencyEnum));
                } catch (IllegalArgumentException e) {
                    // Игнорируем невалидную срочность
                }
            }

            // Фильтр по ID создателя
            if (requesterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("requester").get("id"), requesterId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}