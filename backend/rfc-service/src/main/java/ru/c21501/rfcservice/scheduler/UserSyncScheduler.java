package ru.c21501.rfcservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.c21501.rfcservice.service.UserService;

/**
 * Планировщик для периодической синхронизации пользователей из Keycloak
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class UserSyncScheduler {

    private final UserService userService;

    /**
     * Синхронизирует пользователей из Keycloak в локальную БД
     * Выполняется по расписанию, заданному в cron-выражении
     */
    @Scheduled(cron = "${app.scheduler.user-sync.cron}")
    public void syncUsers() {
        log.info("Starting scheduled user synchronization from Keycloak");
        try {
            userService.syncUsersFromKeycloak();
            log.info("Scheduled user synchronization completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled user synchronization: {}", e.getMessage(), e);
        }
    }
}