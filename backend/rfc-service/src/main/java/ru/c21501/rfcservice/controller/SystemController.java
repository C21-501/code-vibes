package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.request.CreateSystemRequest;
import ru.c21501.rfcservice.dto.request.UpdateSystemRequest;
import ru.c21501.rfcservice.dto.response.SystemResponse;
import ru.c21501.rfcservice.mapper.SystemMapper;
import ru.c21501.rfcservice.model.entity.System;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.service.SystemService;
import ru.c21501.rfcservice.service.TeamService;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с подсистемами
 */
@RestController
@RequestMapping("/api/systems")
@RequiredArgsConstructor
@Slf4j
public class SystemController {

    private final SystemService systemService;
    private final SystemMapper systemMapper;
    private final TeamService teamService;

    /**
     * Получить список всех подсистем
     */
    @GetMapping
    public ResponseEntity<List<SystemResponse>> getAllSystems() {
        log.info("Getting all systems");

        List<System> systems = systemService.findAll();
        List<SystemResponse> response = systems.stream()
                .map(systemMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Получить подсистему по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SystemResponse> getSystemById(@PathVariable UUID id) {
        log.info("Getting system by id: {}", id);

        return systemService.findById(id)
                .map(systemMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить подсистему по названию
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<SystemResponse> getSystemByName(@PathVariable String name) {
        log.info("Getting system by name: {}", name);

        return systemService.findByName(name)
                .map(systemMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить подсистемы по типу
     */
    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<SystemResponse>> getSystemsByType(@PathVariable String type) {
        log.info("Getting systems by type: {}", type);

        List<System> systems = systemService.findByType(type);
        List<SystemResponse> response = systems.stream()
                .map(systemMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Получить подсистемы по ID ответственной команды
     */
    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<List<SystemResponse>> getSystemsByResponsibleTeamId(@PathVariable UUID teamId) {
        log.info("Getting systems by responsible team id: {}", teamId);

        List<System> systems = systemService.findByResponsibleTeamId(teamId);
        List<SystemResponse> response = systems.stream()
                .map(systemMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Создать новую подсистему
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<SystemResponse> createSystem(@Valid @RequestBody CreateSystemRequest request) {
        log.info("Creating system: {}", request.getName());

        Team responsibleTeam = teamService.findById(request.getResponsibleTeamId())
                .orElseThrow(() -> new RuntimeException("Команда не найдена"));

        System system = System.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .responsibleTeam(responsibleTeam)
                .build();

        System savedSystem = systemService.createSystem(system);
        SystemResponse response = systemMapper.toResponse(savedSystem);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Обновить подсистему
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<SystemResponse> updateSystem(@PathVariable UUID id, @Valid @RequestBody UpdateSystemRequest request) {
        log.info("Updating system with id: {}", id);

        if (!systemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Team responsibleTeam = teamService.findById(request.getResponsibleTeamId())
                .orElseThrow(() -> new RuntimeException("Команда не найдена"));

        System system = System.builder()
                .id(id)
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .responsibleTeam(responsibleTeam)
                .build();

        System updatedSystem = systemService.updateSystem(system);
        SystemResponse response = systemMapper.toResponse(updatedSystem);

        return ResponseEntity.ok(response);
    }

    /**
     * Удалить подсистему
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deleteSystem(@PathVariable UUID id) {
        log.info("Deleting system with id: {}", id);

        if (!systemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        systemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
