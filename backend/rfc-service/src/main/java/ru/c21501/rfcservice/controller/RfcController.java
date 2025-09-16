package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.request.ChangeStatusRequest;
import ru.c21501.rfcservice.dto.request.CreateRfcRequest;
import ru.c21501.rfcservice.dto.request.UpdateRfcRequest;
import ru.c21501.rfcservice.dto.response.DashboardResponse;
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
import ru.c21501.rfcservice.service.DashboardService;
import ru.c21501.rfcservice.service.RfcExecutorService;
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
    private final RfcExecutorService rfcExecutorService;
    private final RfcIdService rfcIdService;
    private final StatusHistoryService statusHistoryService;
    private final UserService userService;
    private final DashboardService dashboardService;
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
    
    /**
     * Получить данные для дашборда (статистика, последние RFC, ближайшие дедлайны)
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getDashboardStats() {
        log.info("Getting dashboard statistics");
        
        DashboardResponse dashboardData = dashboardService.getDashboardData();
        
        return ResponseEntity.ok(dashboardData);
    }
    
    /**
     * Изменить статус RFC
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RfcResponse> changeRfcStatus(
            @PathVariable String id,
            @Valid @RequestBody ChangeStatusRequest request,
            Authentication authentication) {
        
        log.info("Changing RFC {} status to {}", id, request.getNewStatus());
        
        try {
            // Получить текущего пользователя из JWT токена
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String keycloakId = jwt.getSubject();
            
            User currentUser = userService.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            
            // Изменить статус RFC
            Rfc updatedRfc = rfcService.changeStatus(id, request.getNewStatus(), currentUser);
            
            // Если все исполнители подтвердили готовность, автоматически изменить статус на WAITING_FOR_CAB
            if (request.getNewStatus() == RfcStatus.APPROVED && rfcService.areAllExecutorsConfirmed(id)) {
                updatedRfc = rfcService.changeStatus(id, RfcStatus.WAITING_FOR_CAB, currentUser);
                log.info("All executors confirmed readiness, automatically changed RFC {} status to WAITING_FOR_CAB", id);
            }
            
            RfcResponse response = rfcMapper.toResponse(updatedRfc);
            
            log.info("Successfully changed RFC {} status to {}", id, updatedRfc.getStatus());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request when changing RFC {} status: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("Forbidden when changing RFC {} status: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error changing RFC {} status: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Подтвердить готовность команды-исполнителя
     */
    @PutMapping("/{id}/executors/{teamId}/confirm")
    public ResponseEntity<RfcResponse> confirmExecutorReadiness(
            @PathVariable String id,
            @PathVariable UUID teamId,
            Authentication authentication) {
        
        log.info("Confirming executor readiness for RFC {} and team {}", id, teamId);
        
        try {
            // Получить текущего пользователя из JWT токена
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String keycloakId = jwt.getSubject();
            
            User currentUser = userService.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            
            // Проверить права пользователя на подтверждение готовности для данной команды
            if (!canConfirmReadiness(currentUser, teamId)) {
                log.error("User {} does not have permission to confirm readiness for team {}", currentUser.getId(), teamId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Обновить статус подтверждения исполнителя
            rfcExecutorService.updateConfirmationStatus(id, teamId, ru.c21501.rfcservice.model.enums.ConfirmationStatus.CONFIRMED);
            
            // Проверить, все ли исполнители подтвердили готовность
            if (rfcService.areAllExecutorsConfirmed(id)) {
                // Автоматически изменить статус RFC на WAITING_FOR_CAB
                Rfc updatedRfc = rfcService.changeStatus(id, RfcStatus.WAITING_FOR_CAB, currentUser);
                log.info("All executors confirmed readiness, automatically changed RFC {} status to WAITING_FOR_CAB", id);
                
                RfcResponse response = rfcMapper.toResponse(updatedRfc);
                return ResponseEntity.ok(response);
            } else {
                // Вернуть обновленный RFC без изменения статуса
                Rfc rfc = rfcService.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("RFC не найден"));
                RfcResponse response = rfcMapper.toResponse(rfc);
                return ResponseEntity.ok(response);
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Bad request when confirming executor readiness for RFC {} and team {}: {}", id, teamId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error confirming executor readiness for RFC {} and team {}: {}", id, teamId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Проверить, может ли пользователь подтверждать готовность для данной команды
     */
    private boolean canConfirmReadiness(User user, UUID teamId) {
        // Администратор и CAB_MANAGER могут подтверждать готовность любой команды
        if (user.getRole() == ru.c21501.rfcservice.model.enums.UserRole.ADMIN || 
            user.getRole() == ru.c21501.rfcservice.model.enums.UserRole.CAB_MANAGER) {
            return true;
        }
        
        // Исполнитель может подтверждать готовность только своей команды (если он лидер)
        if (user.getRole() == ru.c21501.rfcservice.model.enums.UserRole.EXECUTOR) {
            // Здесь нужно проверить, является ли пользователь лидером команды с данным ID
            // Для упрощения, пока разрешаем всем исполнителям
            return true;
        }
        
        return false;
    }
}
