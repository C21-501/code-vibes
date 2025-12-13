package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.UpdateUserRequest;
import ru.c21501.rfcservice.openapi.model.UserRequest;
import ru.c21501.rfcservice.openapi.model.UserResponse;

/**
 * Mapper для преобразования между UserEntity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Преобразует UserRequest в UserEntity
     *
     * @param request запрос с данными пользователя
     * @return сущность пользователя
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "updateDatetime", ignore = true)
    UserEntity toEntity(UserRequest request);

    /**
     * Преобразует UpdateUserRequest в UserEntity
     *
     * @param request запрос с данными для обновления пользователя
     * @return сущность пользователя
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "updateDatetime", ignore = true)
    UserEntity toEntity(UpdateUserRequest request);

    /**
     * Преобразует UserEntity в UserResponse
     *
     * @param entity сущность пользователя
     * @return ответ с данными пользователя
     */
    UserResponse toResponse(UserEntity entity);
}