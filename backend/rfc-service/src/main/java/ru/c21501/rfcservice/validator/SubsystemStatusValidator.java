package ru.c21501.rfcservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.c21501.rfcservice.exception.ValidationException;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;

import java.util.Set;

/**
 * Валидатор переходов статусов подсистем
 */
@Slf4j
@Component
public class SubsystemStatusValidator {

    private static final Set<ConfirmationStatus> ALLOWED_CONFIRMATION_TRANSITIONS_FROM_PENDING =
            Set.of(ConfirmationStatus.CONFIRMED, ConfirmationStatus.REJECTED);

    /**
     * Валидирует переход ConfirmationStatus
     * Правило: можно двигать только из PENDING в CONFIRMED или REJECTED
     */
    public void validateConfirmationStatusTransition(ConfirmationStatus current, ConfirmationStatus target) {
        log.debug("Validating confirmation status transition from {} to {}", current, target);

        if (current == target) {
            log.warn("Attempt to set the same confirmation status: {}", current);
            throw new ValidationException("Статус уже установлен в " + current);
        }

        if (current != ConfirmationStatus.PENDING) {
            log.warn("Invalid confirmation status transition: {} -> {}", current, target);
            throw new ValidationException(
                    String.format("Нельзя изменить статус подтверждения из %s. Изменение возможно только из PENDING", current)
            );
        }

        if (!ALLOWED_CONFIRMATION_TRANSITIONS_FROM_PENDING.contains(target)) {
            log.warn("Invalid target confirmation status: {}", target);
            throw new ValidationException("Статус подтверждения может быть изменен только на CONFIRMED или REJECTED");
        }

        log.debug("Confirmation status transition validated successfully");
    }

    /**
     * Валидирует переход ExecutionStatus
     * Правило: можно двигать только вперед (PENDING → IN_PROGRESS → DONE)
     */
    public void validateExecutionStatusTransition(ExecutionStatus current, ExecutionStatus target) {
        log.debug("Validating execution status transition from {} to {}", current, target);

        if (current == target) {
            log.warn("Attempt to set the same execution status: {}", current);
            throw new ValidationException("Статус уже установлен в " + current);
        }

        // Определяем порядок статусов
        int currentOrder = getExecutionStatusOrder(current);
        int targetOrder = getExecutionStatusOrder(target);

        if (targetOrder <= currentOrder) {
            log.warn("Invalid execution status transition: {} -> {}", current, target);
            throw new ValidationException(
                    String.format("Нельзя вернуться к предыдущему статусу. Текущий: %s, попытка установить: %s", current, target)
            );
        }

        // Проверяем, что переход последовательный (не пропускаем статусы)
        if (targetOrder - currentOrder > 1) {
            log.warn("Attempt to skip execution status: {} -> {}", current, target);
            throw new ValidationException(
                    String.format("Нельзя пропустить промежуточный статус. Текущий: %s, попытка установить: %s", current, target)
            );
        }

        log.debug("Execution status transition validated successfully");
    }

    private int getExecutionStatusOrder(ExecutionStatus status) {
        return switch (status) {
            case PENDING -> 0;
            case IN_PROGRESS -> 1;
            case DONE -> 2;
        };
    }
}
