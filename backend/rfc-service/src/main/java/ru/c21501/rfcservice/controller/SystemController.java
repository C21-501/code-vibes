package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.response.SystemResponse;
import ru.c21501.rfcservice.mapper.SystemMapper;
import ru.c21501.rfcservice.model.entity.System;
import ru.c21501.rfcservice.service.SystemService;

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
}
