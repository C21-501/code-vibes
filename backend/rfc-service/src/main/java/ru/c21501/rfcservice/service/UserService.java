package ru.c21501.rfcservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.c21501.rfcservice.model.entity.UserEntity;

/**
 * Сервис для работы с пользователями (бизнес-логика)
 */
public interface UserService {

    /**
     * Создает нового пользователя в системе и Keycloak
     *
     * @param userEntity данные пользователя
     * @param password   пароль пользователя
     * @return созданный пользователь
     */
    UserEntity createUser(UserEntity userEntity, String password);

    /**
     * Обновляет данные пользователя в системе и Keycloak
     *
     * @param id         ID пользователя
     * @param userEntity новые данные пользователя
     * @return обновленный пользователь
     */
    UserEntity updateUser(Long id, UserEntity userEntity);

    /**
     * Получает пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователь
     */
    UserEntity getUserById(Long id);

    /**
     * Удаляет пользователя из системы и Keycloak
     *
     * @param id ID пользователя
     */
    void deleteUser(Long id);

    /**
     * Получает пагинированный список пользователей с поиском
     *
     * @param searchString строка поиска (опционально)
     * @param pageable     параметры пагинации
     * @return страница пользователей
     */
    Page<UserEntity> getUsers(String searchString, Pageable pageable);
}