package ru.c21501.rfcservice.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.SubsystemsApi;
import ru.c21501.rfcservice.openapi.model.CreateSubsystemRequest;
import ru.c21501.rfcservice.openapi.model.SubsystemResponse;
import ru.c21501.rfcservice.openapi.model.UpdateSubsystemRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SubSystemController implements SubsystemsApi {

    @Override
    public List<SubsystemResponse> systemsIdSubsystemsGet(@NotNull UUID id) {
        return List.of();
    }

    @Override
    public SubsystemResponse systemsIdSubsystemsPost(@NotNull UUID id,
                                                     @NotNull CreateSubsystemRequest createSubsystemRequest) {
        return null;
    }

    @Override
    public void systemsIdSubsystemsSubsystemIdDelete(@NotNull UUID id,
                                                     @NotNull UUID subsystemId) {

    }

    @Override
    public SubsystemResponse systemsIdSubsystemsSubsystemIdGet(@NotNull UUID id,
                                                               @NotNull UUID subsystemId) {
        return null;
    }

    @Override
    public SubsystemResponse systemsIdSubsystemsSubsystemIdPut(@NotNull UUID id,
                                                               @NotNull UUID subsystemId,
                                                               @NotNull UpdateSubsystemRequest updateSubsystemRequest) {
        return null;
    }
}
