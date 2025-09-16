package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.c21501.rfcservice.dto.response.UserResponse;
import ru.c21501.rfcservice.model.entity.User;

/**
 * MapStruct маппер для User
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    /**
     * Преобразование User entity в DTO ответа
     */
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "team", ignore = true)
    UserResponse toResponse(User user);
    
    /**
     * Преобразование User entity в DTO ответа без команды (для избежания циклических зависимостей)
     */
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Named("toResponseWithoutTeam")
    UserResponse toResponseWithoutTeam(User user);
}
