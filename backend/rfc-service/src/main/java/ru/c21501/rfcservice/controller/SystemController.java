package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.SystemsApi;
import ru.c21501.rfcservice.openapi.model.SystemPageResponse;
import ru.c21501.rfcservice.openapi.model.SystemRequest;
import ru.c21501.rfcservice.openapi.model.SystemResponse;
import ru.c21501.rfcservice.service.SystemApiService;

/**
 * Контроллер для работы с системами
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SystemController implements SystemsApi {

    private final SystemApiService systemApiService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public SystemResponse createSystem(SystemRequest systemRequest) {
        log.info("POST /api/systems - Creating system: {}", systemRequest.getName());
        return systemApiService.createSystem(systemRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSystem(Long id) {
        log.info("DELETE /api/systems/{} - Deleting system", id);
        systemApiService.deleteSystem(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemResponse getSystemById(Long id) {
        log.info("GET /api/systems/{} - Getting system by ID", id);
        return systemApiService.getSystemById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemPageResponse getSystems(Integer page, Integer size, String name) {
        log.info("GET /api/systems - Getting systems list (page: {}, size: {}, name: {})", page, size, name);
        return systemApiService.getSystems(page, size, name);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemResponse updateSystem(Long id, SystemRequest systemRequest) {
        log.info("PUT /api/systems/{} - Updating system: {}", id, systemRequest.getName());
        return systemApiService.updateSystem(id, systemRequest);
    }
}
