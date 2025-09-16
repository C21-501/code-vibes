package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.System;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.repository.SystemRepository;
import ru.c21501.rfcservice.service.SystemService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с подсистемами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemServiceImpl implements SystemService {
    
    private final SystemRepository systemRepository;
    
    @Override
    @Transactional
    public System createSystem(System system) {
        log.debug("Создание подсистемы: {}", system.getName());
        System savedSystem = systemRepository.save(system);
        log.info("Создана подсистема с ID: {}", savedSystem.getId());
        return savedSystem;
    }
    
    @Override
    @Transactional
    public System updateSystem(System system) {
        log.debug("Обновление подсистемы с ID: {}", system.getId());
        System updatedSystem = systemRepository.save(system);
        log.info("Обновлена подсистема с ID: {}", updatedSystem.getId());
        return updatedSystem;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<System> findById(UUID id) {
        log.debug("Поиск подсистемы по ID: {}", id);
        return systemRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<System> findByName(String name) {
        log.debug("Поиск подсистемы по названию: {}", name);
        return systemRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<System> findByType(String type) {
        log.debug("Поиск подсистем по типу: {}", type);
        return systemRepository.findByType(type);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<System> findByResponsibleTeam(Team responsibleTeam) {
        log.debug("Поиск подсистем по ответственной команде: {}", responsibleTeam.getId());
        return systemRepository.findByResponsibleTeam(responsibleTeam);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<System> findByResponsibleTeamId(UUID responsibleTeamId) {
        log.debug("Поиск подсистем по ID ответственной команды: {}", responsibleTeamId);
        return systemRepository.findByResponsibleTeamId(responsibleTeamId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<System> findAll() {
        log.debug("Получение всех подсистем");
        return systemRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования подсистемы по ID: {}", id);
        return systemRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.debug("Проверка существования подсистемы по названию: {}", name);
        return systemRepository.existsByName(name);
    }
    
    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Удаление подсистемы по ID: {}", id);
        systemRepository.deleteById(id);
        log.info("Удалена подсистема с ID: {}", id);
    }
}
