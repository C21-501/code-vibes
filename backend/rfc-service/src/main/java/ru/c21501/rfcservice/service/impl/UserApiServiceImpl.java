package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.UserMapper;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.UpdateUserRequest;
import ru.c21501.rfcservice.openapi.model.UserPageResponse;
import ru.c21501.rfcservice.openapi.model.UserRequest;
import ru.c21501.rfcservice.openapi.model.UserResponse;
import ru.c21501.rfcservice.service.UserApiService;
import ru.c21501.rfcservice.service.UserService;

import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с пользователями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserApiServiceImpl implements UserApiService {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("API: Creating user with username: {}", request.getUsername());

        // Прямой маппинг: UserRequest -> UserEntity
        UserEntity userEntity = userMapper.toEntity(request);

        // Вызов бизнес-логики с паролем
        UserEntity createdUser = userService.createUser(userEntity, request.getPassword());

        // Обратный маппинг: UserEntity -> UserResponse
        UserResponse response = userMapper.toResponse(createdUser);

        log.info("API: User created successfully with ID: {}", response.getId());
        return response;
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("API: Updating user with ID: {}", id);

        // Прямой маппинг: UpdateUserRequest -> UserEntity
        UserEntity userEntity = userMapper.toEntity(request);

        // Вызов бизнес-логики
        UserEntity updatedUser = userService.updateUser(id, userEntity);

        // Обратный маппинг: UserEntity -> UserResponse
        UserResponse response = userMapper.toResponse(updatedUser);

        log.info("API: User updated successfully: {}", response.getId());
        return response;
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("API: Getting user by ID: {}", id);

        // Вызов бизнес-логики
        UserEntity user = userService.getUserById(id);

        // Обратный маппинг: UserEntity -> UserResponse
        UserResponse response = userMapper.toResponse(user);

        log.info("API: User retrieved successfully: {}", response.getId());
        return response;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("API: Deleting user with ID: {}", id);

        // Вызов бизнес-логики
        userService.deleteUser(id);

        log.info("API: User deleted successfully: {}", id);
    }

    @Override
    public UserPageResponse getUsers(Integer page, Integer size, String searchString) {
        log.info("API: Getting users - page: {}, size: {}, searchString: {}", page, size, searchString);

        // Устанавливаем значения по умолчанию
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Вызов бизнес-логики
        Page<UserEntity> usersPage = userService.getUsers(searchString, pageable);

        // Маппинг в ответ
        UserPageResponse response = new UserPageResponse();
        response.setContent(usersPage.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList()));
        response.setNumber(usersPage.getNumber());
        response.setSize(usersPage.getSize());
        response.setTotalElements(usersPage.getTotalElements());
        response.setTotalPages(usersPage.getTotalPages());
        response.setFirst(usersPage.isFirst());
        response.setLast(usersPage.isLast());

        log.info("API: Retrieved {} users out of {} total", usersPage.getNumberOfElements(), usersPage.getTotalElements());
        return response;
    }
}