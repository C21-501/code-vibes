package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.c21501.rfcservice.model.entity.TeamEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.TeamRequest;
import ru.c21501.rfcservice.openapi.model.TeamResponse;
import ru.c21501.rfcservice.openapi.model.UserResponse;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper для преобразования между TeamEntity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = UserMapper.class
)
public interface TeamMapper {

    /**
     * Преобразует TeamRequest в TeamEntity
     *
     * @param request запрос с данными команды
     * @return сущность команды
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "updateDatetime", ignore = true)
    TeamEntity toEntity(TeamRequest request);

    /**
     * Преобразует TeamEntity в TeamResponse
     *
     * @param entity сущность команды
     * @return ответ с данными команды
     */
    @Mapping(target = "members", expression = "java(mapMembers(entity.getMembers()))")
    TeamResponse toResponse(TeamEntity entity);

    /**
     * Преобразует Set<UserEntity> в List<UserResponse>
     *
     * @param members набор пользователей
     * @return список пользователей для ответа
     */
    default java.util.List<UserResponse> mapMembers(Set<UserEntity> members) {
        if (members == null) {
            return new java.util.ArrayList<>();
        }
        return members.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует UserEntity в UserResponse
     *
     * @param user сущность пользователя
     * @return ответ с данными пользователя
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "role", source = "role")
    UserResponse mapUserToResponse(UserEntity user);
}
