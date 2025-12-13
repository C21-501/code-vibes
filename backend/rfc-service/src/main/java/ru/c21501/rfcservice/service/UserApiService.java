package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.UpdateUserRequest;
import ru.c21501.rfcservice.openapi.model.UserPageResponse;
import ru.c21501.rfcservice.openapi.model.UserRequest;
import ru.c21501.rfcservice.openapi.model.UserResponse;

/**
 * API-сервис для работы с пользователями (слой маппинга)
 */
public interface UserApiService {

    /**
     * Создает нового пользователя
     *
     * @param request данные для создания пользователя
     * @return созданный пользователь
     */
    UserResponse createUser(UserRequest request);

    /**
     * Обновляет данные пользователя
     *
     * @param id      ID пользователя
     * @param request новые данные пользователя
     * @return обновленный пользователь
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Получает пользователя по ID
     *
     * @param id ID пользователя
     * @return данные пользователя
     */
    UserResponse getUserById(Long id);

    /**
     * Удаляет пользователя
     *
     * @param id ID пользователя
     */
    void deleteUser(Long id);

    /**
     * Получает пагинированный список пользователей с поиском
     *
     * @param page         номер страницы
     * @param size         размер страницы
     * @param searchString строка поиска (опционально)
     * @return страница пользователей
     */
    UserPageResponse getUsers(Integer page, Integer size, String searchString);

    /**
     * Аутентификация пользователя через Keycloak
     *
     * @param request данные для входа (username, password)
     * @return токены доступа
     */
    ru.c21501.rfcservice.openapi.model.LoginResponse loginUser(ru.c21501.rfcservice.openapi.model.LoginRequest request);

    /**
     * Получает информацию о текущем авторизованном пользователе
     *
     * @return данные текущего пользователя
     */
    UserResponse getCurrentUser();
}