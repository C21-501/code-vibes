package ru.c21501.rfcservice.mapper;

import org.mapstruct.*;
import ru.c21501.rfcservice.model.entity.*;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.openapi.model.*;
import ru.c21501.rfcservice.resolver.RfcActionResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper для преобразования между RFC Entity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RfcActionResolver.class}
)
public interface RfcMapper {

    /**
     * Преобразует RfcEntity в RfcResponse
     *
     * @param entity сущность RFC
     * @param currentUser текущий пользователь для определения доступных действий
     * @param actionResolver резолвер для определения доступных действий
     * @return ответ с данными RFC
     */
    @Mapping(target = "requesterId", source = "entity.requester.id")
    @Mapping(target = "affectedSystems", expression = "java(toAffectedSystemsResponse(entity.getAffectedSubsystems()))")
    @Mapping(target = "attachments", expression = "java(toAttachmentResponseList(entity.getAttachments()))")
    @Mapping(target = "actions", expression = "java(actionResolver.resolveActions(entity, currentUser))")
    RfcResponse toResponse(RfcEntity entity, @Context UserEntity currentUser, @Context RfcActionResolver actionResolver);

    /**
     * Преобразует список RfcEntity в список RfcResponse
     *
     * @param entities список сущностей RFC
     * @param currentUser текущий пользователь для определения доступных действий
     * @param actionResolver резолвер для определения доступных действий
     * @return список ответов
     */
    List<RfcResponse> toResponseList(List<RfcEntity> entities, @Context UserEntity currentUser, @Context RfcActionResolver actionResolver);

    /**
     * Преобразует AttachmentEntity в AttachmentResponse
     * Заполняет все необходимые поля, включая информацию о пользователе, загрузившем файл
     *
     * @param entity сущность вложения
     * @return ответ с данными вложения
     */
    @Mapping(target = "rfcId", source = "rfc.id")
    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "uploadedByName", source = "uploadedBy.username")
    AttachmentResponse toAttachmentResponse(AttachmentEntity entity);

    /**
     * Преобразует список AttachmentEntity в список AttachmentResponse
     *
     * @param entities список сущностей вложений
     * @return список ответов с данными вложений
     */
    default List<AttachmentResponse> toAttachmentResponseList(List<AttachmentEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toAttachmentResponse)
                .toList();
    }

    /**
     * Преобразует RfcAffectedSubsystemEntity в AffectedSubsystemResponse
     *
     * @param entity сущность затронутой подсистемы
     * @return ответ с данными затронутой подсистемы
     */
    @Mapping(target = "subsystemId", source = "subsystem.id")
    @Mapping(target = "subsystemName", source = "subsystem.name")
    @Mapping(target = "executorId", source = "executor.id")
    @Mapping(target = "executorName", source = "executor.username")
    AffectedSubsystemResponse toAffectedSubsystemResponse(RfcAffectedSubsystemEntity entity);

    /**
     * Преобразует список RfcAffectedSubsystemEntity в список AffectedSystemResponse
     *
     * Логика работы:
     * 1. Находит все подсистемы, затронутые данным RFC
     * 2. Группирует их по родительской системе
     * 3. Для каждой системы создает AffectedSystemResponse
     * 4. Заполняет информацию о системе и ее затронутых подсистемах
     *
     * @param entities список затронутых подсистем
     * @return список затронутых систем с подсистемами
     */
    default List<AffectedSystemResponse> toAffectedSystemsResponse(List<RfcAffectedSubsystemEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        // Группируем подсистемы по родительской системе
        Map<SystemEntity, List<RfcAffectedSubsystemEntity>> groupedBySystem = entities.stream()
                .collect(Collectors.groupingBy(
                        affectedSubsystem -> affectedSubsystem.getSubsystem().getSystem()
                ));

        // Для каждой системы создаем AffectedSystemResponse
        return groupedBySystem.entrySet().stream()
                .map(entry -> {
                    SystemEntity system = entry.getKey();
                    List<RfcAffectedSubsystemEntity> affectedSubsystems = entry.getValue();

                    // Создаем response для системы
                    AffectedSystemResponse systemResponse = new AffectedSystemResponse();

                    // ID - это ID системы из справочника
                    systemResponse.setId(system.getId());
                    systemResponse.setSystemId(system.getId());
                    systemResponse.setSystemName(system.getName());

                    // Маппим все затронутые подсистемы этой системы
                    List<AffectedSubsystemResponse> subsystemResponses = affectedSubsystems.stream()
                            .map(this::toAffectedSubsystemResponse)
                            .toList();

                    systemResponse.setAffectedSubsystems(subsystemResponses);

                    return systemResponse;
                })
                .toList();
    }

    /**
     * Создает entity истории RFC из RFC entity
     *
     * @param rfc      сущность RFC
     * @param operation тип операции
     * @param changedBy пользователь, выполнивший изменение
     * @return сущность истории RFC
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rfcId", source = "rfc.id")
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "affectedSubsystems", ignore = true)
    @Mapping(target = "attachmentIds", ignore = true)
    RfcHistoryEntity toHistoryEntity(RfcEntity rfc, HistoryOperationType operation, UserEntity changedBy);
}