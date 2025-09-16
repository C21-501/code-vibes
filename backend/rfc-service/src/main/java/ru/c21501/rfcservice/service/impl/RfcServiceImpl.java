package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcIdService;
import ru.c21501.rfcservice.service.RfcService;

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
    private final RfcIdService rfcIdService;
    
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
}
