package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcExecutor;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.enums.ConfirmationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с исполнителями RFC
 */
public interface RfcExecutorService {
    
    /**
     * Создать нового исполнителя RFC
     */
    RfcExecutor createRfcExecutor(RfcExecutor rfcExecutor);
    
    /**
     * Обновить исполнителя RFC
     */
    RfcExecutor updateRfcExecutor(RfcExecutor rfcExecutor);
    
    /**
     * Найти исполнителя по ID
     */
    Optional<RfcExecutor> findById(UUID id);
    
    /**
     * Найти исполнителей по RFC
     */
    List<RfcExecutor> findByRfc(Rfc rfc);
    
    /**
     * Найти исполнителей по ID RFC
     */
    List<RfcExecutor> findByRfcId(String rfcId);
    
    /**
     * Найти исполнителей по команде
     */
    List<RfcExecutor> findByTeam(Team team);
    
    /**
     * Найти исполнителей по ID команды
     */
    List<RfcExecutor> findByTeamId(UUID teamId);
    
    /**
     * Найти исполнителей по статусу подтверждения
     */
    List<RfcExecutor> findByConfirmationStatus(ConfirmationStatus confirmationStatus);
    
    /**
     * Найти конкретного исполнителя по RFC и команде
     */
    Optional<RfcExecutor> findByRfcAndTeam(Rfc rfc, Team team);
    
    /**
     * Найти конкретного исполнителя по ID RFC и ID команды
     */
    Optional<RfcExecutor> findByRfcIdAndTeamId(String rfcId, UUID teamId);
    
    /**
     * Получить всех исполнителей
     */
    List<RfcExecutor> findAll();
    
    /**
     * Проверить существование исполнителя по ID
     */
    boolean existsById(UUID id);
    
    /**
     * Проверить существование исполнителя для RFC и команды
     */
    boolean existsByRfcAndTeam(Rfc rfc, Team team);
    
    /**
     * Проверить существование исполнителя по ID RFC и ID команды
     */
    boolean existsByRfcIdAndTeamId(String rfcId, UUID teamId);
}
