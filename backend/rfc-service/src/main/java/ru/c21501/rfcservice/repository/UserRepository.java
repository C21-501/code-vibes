package ru.c21501.rfcservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Поиск пользователя по id в keycloak (keycloakId)
     *
     * @param keycloakId ID пользователя в Keycloak
     * @return Optional с пользователем или пустой Optional
     */
    Optional<UserEntity> findByKeycloakId(String keycloakId);

    /**
     * Проверка существования пользователя по имени пользователя
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Поиск всех пользователей с определенной ролью
     *
     * @param role роль пользователя
     * @return список пользователей с указанной ролью
     */
    List<UserEntity> findByRole(UserRole role);

    /**
     * Поиск пользователей по строке поиска с пагинацией
     * Поиск осуществляется по ID (если строка - число), username, firstName, lastName
     *
     * @param searchString строка поиска
     * @param pageable     параметры пагинации
     * @return страница пользователей
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "CAST(u.id AS string) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    Page<UserEntity> searchUsers(@Param("searchString") String searchString, Pageable pageable);
}