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

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SystemController implements SystemsApi {

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public SystemResponse createSystem(SystemRequest systemRequest) {
        log.info("createSystem called with: {}", systemRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSystem(Long id) {
        log.info("deleteSystem called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemResponse getSystemById(Long id) {
        log.info("getSystemById called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemPageResponse getSystems(Integer page, Integer size, String name) {
        log.info("getSystems called with page: {}, size: {}, name: {}", page, size, name);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SystemResponse updateSystem(Long id, SystemRequest systemRequest) {
        log.info("updateSystem called with id: {}, systemRequest: {}", id, systemRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
