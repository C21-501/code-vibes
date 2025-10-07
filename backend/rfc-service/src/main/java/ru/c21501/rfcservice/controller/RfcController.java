package ru.c21501.rfcservice.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.openapi.api.RfcApi;
import ru.c21501.rfcservice.openapi.model.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RfcController implements RfcApi {

    @Override
    public RfcPageResponse rfcGet(@NotNull Integer page,
                                  @NotNull Integer size,
                                  @Nullable RfcStatus status,
                                  @Nullable UUID systemId,
                                  @Nullable OffsetDateTime createdFrom,
                                  @Nullable OffsetDateTime createdTo) {
        return null;
    }

    @Override
    public void rfcIdDelete(@NotNull UUID id) {

    }

    @Override
    public RfcResponse rfcIdGet(@NotNull UUID id) {
        return null;
    }

    @Override
    public RfcResponse rfcIdPut(@NotNull UUID id,
                                @NotNull UpdateRfcRequest updateRfcRequest) {
        return null;
    }

    @Override
    public RfcResponse rfcPost(@NotNull CreateRfcRequest createRfcRequest) {
        return null;
    }
}
