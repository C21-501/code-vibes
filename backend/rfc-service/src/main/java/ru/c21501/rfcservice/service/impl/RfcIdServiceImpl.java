package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcIdService;

/**
 * Реализация сервиса для генерации ID RFC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RfcIdServiceImpl implements RfcIdService {
    
    private final RfcRepository rfcRepository;
    
    @Override
    @Transactional(readOnly = true)
    public String generateNextRfcId() {
        log.debug("Генерация следующего ID для RFC");
        
        Integer maxId = rfcRepository.findMaxNumericId();
        int nextId = (maxId == null) ? 1 : maxId + 1;
        
        String rfcId = String.format("RFC-%04d", nextId);
        log.debug("Сгенерирован новый ID RFC: {}", rfcId);
        
        return rfcId;
    }
}
