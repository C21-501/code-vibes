package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.UsersApi;
import ru.c21501.rfcservice.openapi.model.UserPageResponse;
import ru.c21501.rfcservice.openapi.model.UserRequest;
import ru.c21501.rfcservice.openapi.model.UserResponse;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController implements UsersApi {

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(UserRequest userRequest) {
        log.info("createUser called with: {}", userRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(Long id) {
        log.info("deleteUser called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(Long id) {
        log.info("getUserById called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponse getUsers(Integer page, Integer size, String name) {
        log.info("getUsers called with page: {}, size: {}, name: {}", page, size, name);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("updateUser called with id: {}, userRequest: {}", id, userRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}