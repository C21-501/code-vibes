package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.SystemMapper;
import ru.c21501.rfcservice.model.entity.SystemEntity;
import ru.c21501.rfcservice.openapi.model.SystemPageResponse;
import ru.c21501.rfcservice.openapi.model.SystemRequest;
import ru.c21501.rfcservice.openapi.model.SystemResponse;
import ru.c21501.rfcservice.service.SystemApiService;
import ru.c21501.rfcservice.service.SystemService;

import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с системами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemApiServiceImpl implements SystemApiService {

    private final SystemService systemService;
    private final SystemMapper systemMapper;

    @Override
    public SystemResponse createSystem(SystemRequest request) {
        log.info("API: Creating system with name: {}", request.getName());

        // Прямой маппинг: SystemRequest -> SystemEntity
        SystemEntity systemEntity = systemMapper.toEntity(request);

        // Вызов бизнес-логики
        SystemEntity createdSystem = systemService.createSystem(systemEntity);

        // Обратный маппинг: SystemEntity -> SystemResponse
        SystemResponse response = systemMapper.toResponse(createdSystem);

        log.info("API: System created successfully with ID: {}", response.getId());
        return response;
    }

    @Override
    public SystemResponse updateSystem(Long id, SystemRequest request) {
        log.info("API: Updating system with ID: {}", id);

        // Прямой маппинг: SystemRequest -> SystemEntity
        SystemEntity systemEntity = systemMapper.toEntity(request);

        // Вызов бизнес-логики
        SystemEntity updatedSystem = systemService.updateSystem(id, systemEntity);

        // Обратный маппинг: SystemEntity -> SystemResponse
        SystemResponse response = systemMapper.toResponse(updatedSystem);

        log.info("API: System updated successfully: {}", response.getId());
        return response;
    }

    @Override
    public SystemResponse getSystemById(Long id) {
        log.info("API: Getting system by ID: {}", id);

        // Вызов бизнес-логики
        SystemEntity system = systemService.getSystemById(id);

        // Обратный маппинг: SystemEntity -> SystemResponse
        SystemResponse response = systemMapper.toResponse(system);

        log.info("API: System retrieved successfully: {}", response.getId());
        return response;
    }

    @Override
    public void deleteSystem(Long id) {
        log.info("API: Deleting system with ID: {}", id);

        // Вызов бизнес-логики
        systemService.deleteSystem(id);

        log.info("API: System deleted successfully: {}", id);
    }

    @Override
    public SystemPageResponse getSystems(Integer page, Integer size, String searchString) {
        log.info("API: Getting systems - page: {}, size: {}, searchString: {}", page, size, searchString);

        // Устанавливаем значения по умолчанию
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Вызов бизнес-логики
        Page<SystemEntity> systemsPage = systemService.getSystems(searchString, pageable);

        // Маппинг в ответ
        SystemPageResponse response = new SystemPageResponse();
        response.setContent(systemsPage.getContent().stream()
                .map(systemMapper::toResponse)
                .collect(Collectors.toList()));
        response.setNumber(systemsPage.getNumber());
        response.setSize(systemsPage.getSize());
        response.setTotalElements(systemsPage.getTotalElements());
        response.setTotalPages(systemsPage.getTotalPages());
        response.setFirst(systemsPage.isFirst());
        response.setLast(systemsPage.isLast());

        log.info("API: Retrieved {} systems out of {} total", systemsPage.getNumberOfElements(), systemsPage.getTotalElements());
        return response;
    }
}