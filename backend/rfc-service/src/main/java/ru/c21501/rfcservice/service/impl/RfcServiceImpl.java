package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcExecutor;
import ru.c21501.rfcservice.model.entity.StatusHistory;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.ConfirmationStatus;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.repository.RfcExecutorRepository;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcIdService;
import ru.c21501.rfcservice.service.RfcService;
import ru.c21501.rfcservice.service.StatusHistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с RFC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RfcServiceImpl implements RfcService {
    
    private final RfcRepository rfcRepository;
    private final RfcExecutorRepository rfcExecutorRepository;
    private final RfcIdService rfcIdService;
    private final StatusHistoryService statusHistoryService;
    
    @Override
    @Transactional
    public Rfc createRfc(Rfc rfc) {
        log.debug("Создание RFC: {}", rfc.getTitle());
        
        // Генерируем ID, если он не задан
        if (rfc.getId() == null || rfc.getId().trim().isEmpty()) {
            String generatedId = rfcIdService.generateNextRfcId();
            rfc.setId(generatedId);
            log.debug("Сгенерирован ID для RFC: {}", generatedId);
        }
        
        Rfc savedRfc = rfcRepository.save(rfc);
        log.info("Создан RFC с ID: {}", savedRfc.getId());
        return savedRfc;
    }
    
    @Override
    @Transactional
    public Rfc updateRfc(Rfc rfc) {
        log.debug("Обновление RFC с ID: {}", rfc.getId());
        Rfc updatedRfc = rfcRepository.save(rfc);
        log.info("Обновлен RFC с ID: {}", updatedRfc.getId());
        return updatedRfc;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Rfc> findById(String id) {
        log.debug("Поиск RFC по ID: {}", id);
        return rfcRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByStatus(RfcStatus status) {
        log.debug("Поиск RFC по статусу: {}", status);
        return rfcRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByPriority(Priority priority) {
        log.debug("Поиск RFC по приоритету: {}", priority);
        return rfcRepository.findByPriority(priority);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByInitiator(User initiator) {
        log.debug("Поиск RFC по инициатору: {}", initiator.getId());
        return rfcRepository.findByInitiator(initiator);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByInitiatorId(UUID initiatorId) {
        log.debug("Поиск RFC по ID инициатора: {}", initiatorId);
        return rfcRepository.findByInitiatorId(initiatorId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Поиск RFC созданных в диапазоне дат: {} - {}", startDate, endDate);
        return rfcRepository.findByCreatedDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByPlannedDateBetween(LocalDate startDate, LocalDate endDate) {
        log.debug("Поиск RFC с плановой датой в диапазоне: {} - {}", startDate, endDate);
        return rfcRepository.findByPlannedDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByStatusAndPriority(RfcStatus status, Priority priority) {
        log.debug("Поиск RFC по статусу {} и приоритету {}", status, priority);
        return rfcRepository.findByStatusAndPriority(status, priority);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findByTitleContaining(String title) {
        log.debug("Поиск RFC по заголовку содержащему: {}", title);
        return rfcRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Rfc> findAll() {
        log.debug("Получение всех RFC");
        return rfcRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        log.debug("Проверка существования RFC по ID: {}", id);
        return rfcRepository.existsById(id);
    }
    
    @Override
    @Transactional
    public Rfc changeStatus(String rfcId, RfcStatus newStatus, User changedByUser) {
        log.debug("Изменение статуса RFC {} на {}", rfcId, newStatus);
        
        Rfc rfc = findById(rfcId)
                .orElseThrow(() -> new IllegalArgumentException("RFC с ID " + rfcId + " не найден"));
        
        RfcStatus oldStatus = rfc.getStatus();
        
        // Валидация перехода статуса
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new IllegalStateException("Невозможно изменить статус с " + oldStatus + " на " + newStatus);
        }
        
        // Проверка прав пользователя
        if (!canChangeStatus(rfcId, newStatus, changedByUser)) {
            throw new IllegalStateException("У пользователя нет прав для изменения статуса на " + newStatus);
        }
        
        // Изменение статуса
        rfc.setStatus(newStatus);
        Rfc updatedRfc = rfcRepository.save(rfc);
        
        // Создание записи в истории статусов
        StatusHistory statusHistory = StatusHistory.builder()
                .rfc(rfc)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedByUser(changedByUser)
                .build();
        statusHistoryService.createStatusHistory(statusHistory);
        
        log.info("Статус RFC {} изменен с {} на {} пользователем {}", 
                rfcId, oldStatus, newStatus, changedByUser.getId());
        
        return updatedRfc;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canChangeStatus(String rfcId, RfcStatus newStatus, User user) {
        log.debug("Проверка прав пользователя {} для изменения статуса на {}", user.getId(), newStatus);
        
        Rfc rfc = findById(rfcId)
                .orElseThrow(() -> new IllegalArgumentException("RFC с ID " + rfcId + " не найден"));
        
        // Администратор и CAB_MANAGER могут изменять любые статусы
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.CAB_MANAGER) {
            return true;
        }
        
        // Инициатор может отменить свой RFC если он еще не одобрен
        if (user.getId().equals(rfc.getInitiator().getId()) && 
            newStatus == RfcStatus.CANCELLED &&
            (rfc.getStatus() == RfcStatus.REQUESTED_NEW || rfc.getStatus() == RfcStatus.WAITING)) {
            return true;
        }
        
        // Исполнители могут подтверждать готовность (изменение статуса на WAITING_FOR_CAB происходит автоматически)
        if (user.getRole() == UserRole.EXECUTOR && newStatus == RfcStatus.DONE) {
            // Проверяем, является ли пользователь лидером одной из команд-исполнителей данного RFC
            List<RfcExecutor> executors = rfcExecutorRepository.findByRfcId(rfcId);
            return executors.stream()
                    .anyMatch(executor -> executor.getTeam().getLeader().getId().equals(user.getId()));
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean areAllExecutorsConfirmed(String rfcId) {
        log.debug("Проверка подтверждения всех исполнителей для RFC {}", rfcId);
        
        List<RfcExecutor> executors = rfcExecutorRepository.findByRfcId(rfcId);
        
        if (executors.isEmpty()) {
            return false;
        }
        
        return executors.stream()
                .allMatch(executor -> executor.getConfirmationStatus() == ConfirmationStatus.CONFIRMED);
    }
    
    /**
     * Проверяет валидность перехода статуса
     */
    private boolean isValidStatusTransition(RfcStatus from, RfcStatus to) {
        if (from == to) {
            return false; // Нельзя изменить статус на тот же самый
        }
        
        return switch (from) {
            case REQUESTED_NEW -> to == RfcStatus.WAITING || to == RfcStatus.CANCELLED;
            case WAITING -> to == RfcStatus.APPROVED || to == RfcStatus.DECLINED || to == RfcStatus.CANCELLED;
            case APPROVED -> to == RfcStatus.WAITING_FOR_CAB || to == RfcStatus.DONE || to == RfcStatus.CANCELLED;
            case WAITING_FOR_CAB -> to == RfcStatus.DONE || to == RfcStatus.DECLINED || to == RfcStatus.CANCELLED;
            case DECLINED -> to == RfcStatus.WAITING || to == RfcStatus.CANCELLED;
            case DONE -> false; // Из DONE нельзя переходить в другие статусы
            case CANCELLED -> false; // Из CANCELLED нельзя переходить в другие статусы
        };
    }
}
