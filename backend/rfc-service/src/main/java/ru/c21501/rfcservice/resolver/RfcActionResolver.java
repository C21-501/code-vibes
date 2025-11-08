package ru.c21501.rfcservice.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.openapi.model.RfcAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Резолвер для определения доступных действий над RFC для пользователя
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RfcActionResolver {

    /**
     * Роли, которые могут согласовывать RFC
     */
    private static final Set<UserRole> APPROVER_ROLES = Set.of(
            UserRole.RFC_APPROVER,
            UserRole.CAB_MANAGER,
            UserRole.ADMIN
    );

    /**
     * Роли с административными привилегиями
     */
    private static final Set<UserRole> ADMIN_ROLES = Set.of(
            UserRole.CAB_MANAGER,
            UserRole.ADMIN
    );

    /**
     * Определяет список доступных действий для пользователя над RFC
     *
     * @param rfc  сущность RFC
     * @param user текущий пользователь
     * @return список доступных действий
     */
    public List<RfcAction> resolveActions(RfcEntity rfc, UserEntity user) {
        if (rfc == null || user == null) {
            log.warn("RFC or User is null, returning empty actions");
            return List.of();
        }

        List<RfcAction> actions = new ArrayList<>();

        // Проверяем права на UPDATE и DELETE
        if (canModifyRfc(rfc, user)) {
            actions.add(RfcAction.UPDATE);
            actions.add(RfcAction.DELETE);
        }

        // Проверяем права на APPROVE
        if (canApproveRfc(user)) {
            actions.add(RfcAction.APPROVE);
        }

        log.debug("Resolved actions for RFC {} and user {}: {}",
                rfc.getId(), user.getUsername(), actions);

        return actions;
    }

    /**
     * Проверяет, может ли пользователь изменять или удалять RFC
     *
     * Логика:
     * - Пользователи с ролями CAB_MANAGER или ADMIN могут изменять любые RFC
     * - Создатель RFC (requester) может изменять свой RFC независимо от роли
     *
     * @param rfc  сущность RFC
     * @param user пользователь
     * @return true, если пользователь может изменять RFC
     */
    private boolean canModifyRfc(RfcEntity rfc, UserEntity user) {
        // Проверяем административные роли
        if (ADMIN_ROLES.contains(user.getRole())) {
            return true;
        }

        // Проверяем, является ли пользователь создателем RFC
        return rfc.getRequester() != null
                && rfc.getRequester().getId().equals(user.getId());
    }

    /**
     * Проверяет, может ли пользователь согласовывать RFC
     *
     * Логика:
     * - RFC_APPROVER, CAB_MANAGER, ADMIN могут согласовывать RFC
     *
     * @param user пользователь
     * @return true, если пользователь может согласовывать RFC
     */
    private boolean canApproveRfc(UserEntity user) {
        return APPROVER_ROLES.contains(user.getRole());
    }
}
