package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.StatusHistory;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.repository.StatusHistoryRepository;
import ru.c21501.rfcservice.service.StatusHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с историей статусов RFC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatusHistoryServiceImpl implements StatusHistoryService {
    
    private final StatusHistoryRepository statusHistoryRepository;
    
    @Override
    @Transactional
    public StatusHistory createStatusHistory(StatusHistory statusHistory) {
        log.debug("Создание записи истории для RFC: {} со статусом: {}", 
                  statusHistory.getRfc().getId(), statusHistory.getNewStatus());
        StatusHistory savedStatusHistory = statusHistoryRepository.save(statusHistory);
        log.info("Создана запись истории с ID: {}", savedStatusHistory.getId());
        return savedStatusHistory;
    }
    
    @Override
    @Transactional
    public StatusHistory updateStatusHistory(StatusHistory statusHistory) {
        log.debug("Обновление записи истории с ID: {}", statusHistory.getId());
        StatusHistory updatedStatusHistory = statusHistoryRepository.save(statusHistory);
        log.info("Обновлена запись истории с ID: {}", updatedStatusHistory.getId());
        return updatedStatusHistory;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StatusHistory> findById(UUID id) {
        log.debug("Поиск записи истории по ID: {}", id);
        return statusHistoryRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByRfc(Rfc rfc) {
        log.debug("Поиск истории по RFC: {}", rfc.getId());
        return statusHistoryRepository.findByRfc(rfc);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByRfcId(String rfcId) {
        log.debug("Поиск истории по ID RFC: {}", rfcId);
        return statusHistoryRepository.findByRfcId(rfcId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByRfcOrderByChangeDateDesc(Rfc rfc) {
        log.debug("Поиск истории по RFC: {} (отсортированной по дате)", rfc.getId());
        return statusHistoryRepository.findByRfcOrderByChangeDateDesc(rfc);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByRfcIdOrderByChangeDateDesc(String rfcId) {
        log.debug("Поиск истории по ID RFC: {} (отсортированной по дате)", rfcId);
        return statusHistoryRepository.findByRfcIdOrderByChangeDateDesc(rfcId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByOldStatus(RfcStatus oldStatus) {
        log.debug("Поиск истории по старому статусу: {}", oldStatus);
        return statusHistoryRepository.findByOldStatus(oldStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByNewStatus(RfcStatus newStatus) {
        log.debug("Поиск истории по новому статусу: {}", newStatus);
        return statusHistoryRepository.findByNewStatus(newStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByChangedByUser(User changedByUser) {
        log.debug("Поиск истории по пользователю: {}", changedByUser.getId());
        return statusHistoryRepository.findByChangedByUser(changedByUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByChangedByUserId(UUID changedByUserId) {
        log.debug("Поиск истории по ID пользователя: {}", changedByUserId);
        return statusHistoryRepository.findByChangedByUserId(changedByUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findByChangeDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Поиск истории в диапазоне дат: {} - {}", startDate, endDate);
        return statusHistoryRepository.findByChangeDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StatusHistory> findAll() {
        log.debug("Получение всей истории");
        return statusHistoryRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования записи истории по ID: {}", id);
        return statusHistoryRepository.existsById(id);
    }
}
