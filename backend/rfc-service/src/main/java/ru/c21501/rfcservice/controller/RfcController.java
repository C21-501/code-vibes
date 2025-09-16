package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.request.CreateRfcRequest;
import ru.c21501.rfcservice.dto.request.UpdateRfcRequest;
import ru.c21501.rfcservice.dto.response.RfcResponse;
import ru.c21501.rfcservice.dto.response.StatusHistoryResponse;
import ru.c21501.rfcservice.mapper.RfcMapper;
import ru.c21501.rfcservice.mapper.StatusHistoryMapper;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.StatusHistory;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcIdService;
import ru.c21501.rfcservice.service.RfcService;
import ru.c21501.rfcservice.service.StatusHistoryService;
import ru.c21501.rfcservice.service.UserService;
import ru.c21501.rfcservice.specification.RfcSpecification;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с RFC
 */
@RestController
@RequestMapping("/api/rfcs")
@RequiredArgsConstructor
@Slf4j
public class RfcController {
    
    private final RfcService rfcService;
    private final RfcIdService rfcIdService;
    private final StatusHistoryService statusHistoryService;
    private final UserService userService;
    private final RfcRepository rfcRepository;
    private final RfcMapper rfcMapper;
    private final StatusHistoryMapper statusHistoryMapper;
    
    /**
     * Получить список RFC с пагинацией, сортировкой и фильтрацией
     */
    @GetMapping
    public ResponseEntity<Page<RfcResponse>> getAllRfcs(
            @RequestParam(required = false) RfcStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) UUID initiatorId,
            @RequestParam(required = false) String title,
            Pageable pageable) {
        
        log.info("Getting RFCs with filters: status={}, priority={}, initiatorId={}, title={}", 
                status, priority, initiatorId, title);
        
        Specification<Rfc> spec = Specification.where(RfcSpecification.hasStatus(status))
                .and(RfcSpecification.hasPriority(priority))
                .and(RfcSpecification.hasInitiatorId(initiatorId))
                .and(RfcSpecification.titleContains(title));
        
        Page<Rfc> rfcs = rfcRepository.findAll(spec, pageable);
        Page<RfcResponse> response = rfcs.map(rfcMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить RFC по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RfcResponse> getRfcById(@PathVariable String id) {
        log.info("Getting RFC by id: {}", id);
        
        return rfcService.findById(id)
                .map(rfcMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Создать новое RFC
     */
    @PostMapping
    public ResponseEntity<RfcResponse> createRfc(@Valid @RequestBody CreateRfcRequest request) {
        log.info("Creating new RFC with title: {}", request.getTitle());
        
        // Найти инициатора
        User initiator = userService.findById(request.getInitiatorId())
                .orElseThrow(() -> new IllegalArgumentException("Инициатор с ID " + request.getInitiatorId() + " не найден"));
        
        // Создать RFC entity
        Rfc rfc = rfcMapper.toEntity(request);
        rfc.setId(rfcIdService.generateNextRfcId());
        rfc.setInitiator(initiator);
        
        // Сохранить RFC
        Rfc savedRfc = rfcService.createRfc(rfc);
        RfcResponse response = rfcMapper.toResponse(savedRfc);
        
        log.info("Created RFC with id: {}", savedRfc.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Обновить RFC
     */
    @PutMapping("/{id}")
    public ResponseEntity<RfcResponse> updateRfc(
            @PathVariable String id,
            @Valid @RequestBody UpdateRfcRequest request) {
        
        log.info("Updating RFC with id: {}", id);
        
        return rfcService.findById(id)
                .map(existingRfc -> {
                    // Проверить, можно ли обновлять RFC в текущем статусе
                    if (existingRfc.getStatus() == RfcStatus.DONE || 
                        existingRfc.getStatus() == RfcStatus.CANCELLED) {
                        throw new IllegalStateException("Нельзя обновлять RFC в статусе " + existingRfc.getStatus());
                    }
                    
                    // Обновить поля
                    rfcMapper.updateEntityFromRequest(request, existingRfc);
                    
                    // Сохранить обновленный RFC
                    Rfc updatedRfc = rfcService.updateRfc(existingRfc);
                    RfcResponse response = rfcMapper.toResponse(updatedRfc);
                    
                    log.info("Updated RFC with id: {}", id);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получить историю статусов RFC
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<StatusHistoryResponse>> getRfcStatusHistory(@PathVariable String id) {
        log.info("Getting status history for RFC id: {}", id);
        
        // Проверить существование RFC
        if (!rfcService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        List<StatusHistory> history = statusHistoryService.findByRfcIdOrderByChangeDateDesc(id);
        List<StatusHistoryResponse> response = history.stream()
                .map(statusHistoryMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(response);
    }
}
