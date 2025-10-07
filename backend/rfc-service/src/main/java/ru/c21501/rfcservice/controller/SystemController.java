package ru.c21501.rfcservice.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.mapper.SystemMapper;
import ru.c21501.rfcservice.openapi.api.SystemsApi;
import ru.c21501.rfcservice.openapi.model.CreateSystemRequest;
import ru.c21501.rfcservice.openapi.model.SystemPageResponse;
import ru.c21501.rfcservice.openapi.model.SystemResponse;
import ru.c21501.rfcservice.openapi.model.UpdateSystemRequest;
import ru.c21501.rfcservice.service.SystemService;
import ru.c21501.rfcservice.service.TeamService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SystemController implements SystemsApi {

    private final SystemService systemService;
    private final SystemMapper systemMapper;
    private final TeamService teamService;

    @Override
    public SystemPageResponse systemsGet(@NotNull Integer page,
                                         @NotNull Integer size,
                                         @Nullable String name) {
        return null;
    }

    @Override
    public void systemsIdDelete(@NotNull UUID id) {

    }

    @Override
    public SystemResponse systemsIdGet(@NotNull UUID id) {
        return null;
    }

    @Override
    public SystemResponse systemsIdPut(@NotNull UUID id,
                                       @NotNull UpdateSystemRequest updateSystemRequest) {
        return null;
    }

    @Override
    public SystemResponse systemsPost(@NotNull CreateSystemRequest createSystemRequest) {
        return null;
    }
}
