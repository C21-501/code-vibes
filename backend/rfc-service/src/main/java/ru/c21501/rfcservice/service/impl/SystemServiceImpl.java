package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.exception.SystemAlreadyExistsException;
import ru.c21501.rfcservice.model.entity.SystemEntity;
import ru.c21501.rfcservice.repository.SystemRepository;
import ru.c21501.rfcservice.service.SystemService;

/**
 * Реализация сервиса для работы с системами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final SystemRepository systemRepository;

    @Override
    @Transactional
    public SystemEntity createSystem(SystemEntity systemEntity) {
        log.info("Creating system: {}", systemEntity.getName());

        // Проверяем, что система с таким названием еще не существует
        if (systemRepository.existsByName(systemEntity.getName())) {
            throw new SystemAlreadyExistsException(
                    String.format("System with name '%s' already exists", systemEntity.getName())
            );
        }

        // Сохраняем систему в БД
        SystemEntity savedSystem = systemRepository.save(systemEntity);
        log.info("System saved to database with ID: {}", savedSystem.getId());

        return savedSystem;
    }

    @Override
    @Transactional
    public SystemEntity updateSystem(Long id, SystemEntity systemEntity) {
        log.info("Updating system with ID: {}", id);

        // Проверяем, что система существует
        SystemEntity existingSystem = getSystemById(id);

        // Проверяем, что новое название не занято другой системой
        if (!existingSystem.getName().equals(systemEntity.getName())
                && systemRepository.existsByName(systemEntity.getName())) {
            throw new SystemAlreadyExistsException(
                    String.format("System with name '%s' already exists", systemEntity.getName())
            );
        }

        // Обновляем данные
        existingSystem.setName(systemEntity.getName());
        existingSystem.setDescription(systemEntity.getDescription());

        // Сохраняем обновленную систему
        SystemEntity updatedSystem = systemRepository.save(existingSystem);
        log.info("System updated successfully: {}", updatedSystem.getId());

        return updatedSystem;
    }

    @Override
    @Transactional(readOnly = true)
    public SystemEntity getSystemById(Long id) {
        log.info("Getting system by ID: {}", id);
        return systemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("System with ID %d not found", id)
                ));
    }

    @Override
    @Transactional
    public void deleteSystem(Long id) {
        log.info("Deleting system with ID: {}", id);

        // Проверяем, что система существует
        SystemEntity system = getSystemById(id);

        // Удаляем систему из БД (подсистемы удалятся автоматически благодаря CASCADE)
        systemRepository.delete(system);
        log.info("System deleted from database: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemEntity> getSystems(String searchString, Pageable pageable) {
        log.info("Getting systems with searchString: {}, page: {}, size: {}",
                searchString, pageable.getPageNumber(), pageable.getPageSize());

        Page<SystemEntity> systems;

        if (searchString != null && !searchString.trim().isEmpty()) {
            // Поиск по строке
            systems = systemRepository.searchSystems(searchString.trim(), pageable);
            log.info("Found {} systems matching search string '{}'", systems.getTotalElements(), searchString);
        } else {
            // Получить все системы
            systems = systemRepository.findAll(pageable);
            log.info("Retrieved all systems, total: {}", systems.getTotalElements());
        }

        return systems;
    }
}