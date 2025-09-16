package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.c21501.rfcservice.dto.response.TeamResponse;
import ru.c21501.rfcservice.model.entity.Team;

/**
 * MapStruct маппер для Team
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeamMapper {
    
    /**
     * Преобразование Team entity в DTO ответа
     */
    @Mapping(target = "leader", source = "leader", qualifiedByName = "toResponseWithoutTeam")
    TeamResponse toResponse(Team team);
}
