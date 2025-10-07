package ru.c21501.rfcservice.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.openapi.api.FilesApi;
import ru.c21501.rfcservice.openapi.model.FileResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController implements FilesApi {

    @Override
    public void filesIdDelete(@NotNull UUID id) {

    }

    @Override
    public FileResponse filesPost(@NotNull MultipartFile file) {
        return null;
    }
}
