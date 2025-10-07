package ru.c21501.rfcservice.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.mapper.UserMapper;
import ru.c21501.rfcservice.openapi.api.UsersApi;
import ru.c21501.rfcservice.openapi.model.UserPageResponse;
import ru.c21501.rfcservice.openapi.model.UserResponse;
import ru.c21501.rfcservice.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserPageResponse usersGet(@NotNull Integer page,
                                     @NotNull Integer size,
                                     @Nullable String name) {
        return null;
    }

    @Override
    public UserResponse usersIdGet(@NotNull UUID id) {
        return null;
    }
}
