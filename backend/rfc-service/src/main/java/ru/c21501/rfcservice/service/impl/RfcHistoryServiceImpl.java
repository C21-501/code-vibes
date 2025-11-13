package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.dto.response.history.AttachmentInfo;
import ru.c21501.rfcservice.dto.response.history.FieldChange;
import ru.c21501.rfcservice.dto.response.history.RfcAttachmentsChangedEvent;
import ru.c21501.rfcservice.dto.response.history.RfcFieldsChangedEvent;
import ru.c21501.rfcservice.dto.response.history.RfcSubsystemsChangedEvent;
import ru.c21501.rfcservice.dto.response.history.SubsystemInfo;
import ru.c21501.rfcservice.dto.response.history.SubsystemStatusChangedEvent;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemHistoryEntity;
import ru.c21501.rfcservice.model.entity.RfcHistoryEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.openapi.model.HistoryUser;
import ru.c21501.rfcservice.openapi.model.RfcHistoryEvent;
import ru.c21501.rfcservice.openapi.model.RfcHistoryResponse;
import ru.c21501.rfcservice.repository.AttachmentRepository;
import ru.c21501.rfcservice.repository.RfcAffectedSubsystemHistoryRepository;
import ru.c21501.rfcservice.repository.RfcAffectedSubsystemRepository;
import ru.c21501.rfcservice.repository.RfcHistoryRepository;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcHistoryService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с историей изменений RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcHistoryServiceImpl implements RfcHistoryService {

    private final RfcRepository rfcRepository;
    private final RfcHistoryRepository rfcHistoryRepository;
    private final RfcAffectedSubsystemHistoryRepository subsystemHistoryRepository;
    private final RfcAffectedSubsystemRepository affectedSubsystemRepository;
    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional(readOnly = true)
    public RfcHistoryResponse getRfcHistory(Long rfcId, Pageable pageable) {
        log.info("Getting RFC history: rfcId={}, page={}, size={}", rfcId, pageable.getPageNumber(), pageable.getPageSize());

        // Проверяем существование RFC
        if (!rfcRepository.existsById(rfcId)) {
            throw new ResourceNotFoundException("RFC not found with id: " + rfcId);
        }

        // 1. Получаем историю RFC
        List<RfcHistoryEntity> rfcHistory = rfcHistoryRepository.findAllByRfcIdWithUsers(rfcId);

        // 2. Получаем все affected subsystems для этого RFC (текущие и исторические ID)
        Set<Long> affectedSubsystemIds = extractAffectedSubsystemIds(rfcHistory);

        // 3. Получаем историю изменений статусов подсистем
        List<RfcAffectedSubsystemHistoryEntity> subsystemHistory = Collections.emptyList();
        if (!affectedSubsystemIds.isEmpty()) {
            subsystemHistory = subsystemHistoryRepository
                    .findByRfcAffectedSubsystemIdInWithUsers(new ArrayList<>(affectedSubsystemIds));
        }

        // 4. Создаем unified timeline из всех событий
        List<RfcHistoryEvent> events = new ArrayList<>();

        // 4.1 Обрабатываем RFC history и создаем события
        events.addAll(processRfcHistory(rfcHistory));

        // 4.2 Обрабатываем subsystem status history
        events.addAll(processSubsystemHistory(subsystemHistory, affectedSubsystemIds));

        // 5. Сортируем по времени (DESC - новые сначала)
        events.sort((e1, e2) -> {
            OffsetDateTime t1 = e1.getTimestamp();
            OffsetDateTime t2 = e2.getTimestamp();
            return t2.compareTo(t1);
        });

        // 6. Применяем пагинацию вручную
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), events.size());

        List<RfcHistoryEvent> paginatedEvents = start < events.size()
                ? events.subList(start, end)
                : Collections.emptyList();

        // 7. Формируем ответ
        return buildHistoryResponse(paginatedEvents, events.size(), pageable);
    }

    /**
     * Извлечь все ID affected subsystems из истории RFC
     */
    private Set<Long> extractAffectedSubsystemIds(List<RfcHistoryEntity> rfcHistory) {
        Set<Long> ids = new HashSet<>();
        for (RfcHistoryEntity history : rfcHistory) {
            if (history.getAffectedSubsystems() != null) {
                ids.addAll(history.getAffectedSubsystems());
            }
        }
        return ids;
    }

    /**
     * Обработать историю RFC и создать события изменений
     */
    private List<RfcHistoryEvent> processRfcHistory(List<RfcHistoryEntity> rfcHistory) {
        List<RfcHistoryEvent> events = new ArrayList<>();

        for (int i = 0; i < rfcHistory.size(); i++) {
            RfcHistoryEntity current = rfcHistory.get(i);
            RfcHistoryEntity previous = (i < rfcHistory.size() - 1) ? rfcHistory.get(i + 1) : null;

            HistoryUser changedBy = mapToHistoryUser(current.getChangedBy());

            if (current.getOperation() == HistoryOperationType.CREATE) {
                // При создании - все поля новые
                events.add(createRfcFieldsChangedEvent(current, null, changedBy));
            } else {
                // При обновлении - вычисляем diff
                if (previous != null) {
                    // Изменения полей RFC
                    RfcFieldsChangedEvent fieldsEvent = computeFieldsDiff(current, previous, changedBy);
                    if (fieldsEvent != null && !fieldsEvent.getChanges().isEmpty()) {
                        events.add(fieldsEvent);
                    }

                    // Изменения attachments
                    RfcAttachmentsChangedEvent attachmentsEvent = computeAttachmentsDiff(current, previous, changedBy);
                    if (attachmentsEvent != null &&
                            (!attachmentsEvent.getAttachmentsAdded().isEmpty() ||
                                    !attachmentsEvent.getAttachmentsRemoved().isEmpty())) {
                        events.add(attachmentsEvent);
                    }

                    // Изменения subsystems
                    RfcSubsystemsChangedEvent subsystemsEvent = computeSubsystemsDiff(current, previous, changedBy);
                    if (subsystemsEvent != null &&
                            (!subsystemsEvent.getSubsystemsAdded().isEmpty() ||
                                    !subsystemsEvent.getSubsystemsRemoved().isEmpty())) {
                        events.add(subsystemsEvent);
                    }
                }
            }
        }

        return events;
    }

    /**
     * Создать событие для CREATE операции
     */
    private RfcFieldsChangedEvent createRfcFieldsChangedEvent(RfcHistoryEntity current,
                                                              RfcHistoryEntity previous,
                                                              HistoryUser changedBy) {
        Map<String, FieldChange> changes = new HashMap<>();

        // При создании все поля - новые
        changes.put("title", FieldChange.builder().oldValue(null).newValue(current.getTitle()).build());
        changes.put("description", FieldChange.builder().oldValue(null).newValue(current.getDescription()).build());
        changes.put("implementationDate", FieldChange.builder().oldValue(null).newValue(current.getImplementationDate().toString()).build());
        changes.put("urgency", FieldChange.builder().oldValue(null).newValue(current.getUrgency().toString()).build());
        changes.put("status", FieldChange.builder().oldValue(null).newValue(current.getStatus().toString()).build());

        return new RfcFieldsChangedEvent(
                "RFC_FIELDS_CHANGED",
                current.getCreateDatetime(),
                changedBy,
                RfcFieldsChangedEvent.OperationEnum.CREATE,
                changes
        );
    }

    /**
     * Вычислить diff полей между двумя snapshot'ами
     */
    private RfcFieldsChangedEvent computeFieldsDiff(RfcHistoryEntity current,
                                                    RfcHistoryEntity previous,
                                                    HistoryUser changedBy) {
        Map<String, FieldChange> changes = new HashMap<>();

        // Сравниваем поля
        if (!Objects.equals(current.getTitle(), previous.getTitle())) {
            changes.put("title", FieldChange.builder()
                    .oldValue(previous.getTitle())
                    .newValue(current.getTitle())
                    .build());
        }

        if (!Objects.equals(current.getDescription(), previous.getDescription())) {
            changes.put("description", FieldChange.builder()
                    .oldValue(previous.getDescription())
                    .newValue(current.getDescription())
                    .build());
        }

        if (!Objects.equals(current.getImplementationDate(), previous.getImplementationDate())) {
            changes.put("implementationDate", FieldChange.builder()
                    .oldValue(previous.getImplementationDate().toString())
                    .newValue(current.getImplementationDate().toString())
                    .build());
        }

        if (current.getUrgency() != previous.getUrgency()) {
            changes.put("urgency", FieldChange.builder()
                    .oldValue(previous.getUrgency().toString())
                    .newValue(current.getUrgency().toString())
                    .build());
        }

        if (current.getStatus() != previous.getStatus()) {
            changes.put("status", FieldChange.builder()
                    .oldValue(previous.getStatus().toString())
                    .newValue(current.getStatus().toString())
                    .build());
        }

        if (changes.isEmpty()) {
            return null;
        }

        return new RfcFieldsChangedEvent(
                "RFC_FIELDS_CHANGED",
                current.getCreateDatetime(),
                changedBy,
                RfcFieldsChangedEvent.OperationEnum.UPDATE,
                changes
        );
    }

    /**
     * Вычислить diff attachments
     */
    private RfcAttachmentsChangedEvent computeAttachmentsDiff(RfcHistoryEntity current,
                                                              RfcHistoryEntity previous,
                                                              HistoryUser changedBy) {
        Set<Long> currentIds = current.getAttachmentIds() != null ? current.getAttachmentIds() : Collections.emptySet();
        Set<Long> previousIds = previous.getAttachmentIds() != null ? previous.getAttachmentIds() : Collections.emptySet();

        Set<Long> added = new HashSet<>(currentIds);
        added.removeAll(previousIds);

        Set<Long> removed = new HashSet<>(previousIds);
        removed.removeAll(currentIds);

        if (added.isEmpty() && removed.isEmpty()) {
            return null;
        }

        // Получаем информацию о файлах
        List<AttachmentInfo> addedInfos = getAttachmentInfos(added);
        List<AttachmentInfo> removedInfos = getAttachmentInfos(removed);

        return new RfcAttachmentsChangedEvent(
                "RFC_ATTACHMENTS_CHANGED",
                current.getCreateDatetime(),
                changedBy,
                addedInfos,
                removedInfos
        );
    }

    /**
     * Вычислить diff subsystems
     */
    private RfcSubsystemsChangedEvent computeSubsystemsDiff(RfcHistoryEntity current,
                                                            RfcHistoryEntity previous,
                                                            HistoryUser changedBy) {
        Set<Long> currentIds = current.getAffectedSubsystems() != null ? current.getAffectedSubsystems() : Collections.emptySet();
        Set<Long> previousIds = previous.getAffectedSubsystems() != null ? previous.getAffectedSubsystems() : Collections.emptySet();

        Set<Long> added = new HashSet<>(currentIds);
        added.removeAll(previousIds);

        Set<Long> removed = new HashSet<>(previousIds);
        removed.removeAll(currentIds);

        if (added.isEmpty() && removed.isEmpty()) {
            return null;
        }

        // Получаем информацию о подсистемах
        List<SubsystemInfo> addedInfos = getSubsystemInfos(added);
        List<SubsystemInfo> removedInfos = getSubsystemInfos(removed);

        return new RfcSubsystemsChangedEvent(
                "RFC_SUBSYSTEMS_CHANGED",
                current.getCreateDatetime(),
                changedBy,
                addedInfos,
                removedInfos
        );
    }

    /**
     * Обработать историю изменений статусов подсистем
     */
    private List<RfcHistoryEvent> processSubsystemHistory(
            List<RfcAffectedSubsystemHistoryEntity> subsystemHistory,
            Set<Long> affectedSubsystemIds) {

        List<RfcHistoryEvent> events = new ArrayList<>();

        // Получаем информацию о подсистемах один раз
        Map<Long, RfcAffectedSubsystemEntity> subsystemMap = affectedSubsystemRepository
                .findAllById(affectedSubsystemIds)
                .stream()
                .collect(Collectors.toMap(RfcAffectedSubsystemEntity::getId, s -> s));

        for (RfcAffectedSubsystemHistoryEntity history : subsystemHistory) {
            RfcAffectedSubsystemEntity affectedSubsystem = subsystemMap.get(history.getRfcAffectedSubsystemId());
            if (affectedSubsystem == null) {
                continue;
            }

            // Создаем SubsystemInfo
            ru.c21501.rfcservice.dto.response.history.SubsystemInfo subsystemInfo = ru.c21501.rfcservice.dto.response.history.SubsystemInfo.builder()
                    .id(affectedSubsystem.getId())
                    .subsystemId(affectedSubsystem.getSubsystem().getId())
                    .subsystemName(affectedSubsystem.getSubsystem().getName())
                    .systemName(affectedSubsystem.getSubsystem().getSystem().getName())
                    .executorId(affectedSubsystem.getExecutor().getId())
                    .executorName(affectedSubsystem.getExecutor().getFirstName() + " " + affectedSubsystem.getExecutor().getLastName())
                    .build();

            SubsystemStatusChangedEvent.StatusTypeEnum statusType =
                    "CONFIRMATION".equals(history.getStatusType())
                            ? SubsystemStatusChangedEvent.StatusTypeEnum.CONFIRMATION
                            : SubsystemStatusChangedEvent.StatusTypeEnum.EXECUTION;

            SubsystemStatusChangedEvent event = new SubsystemStatusChangedEvent(
                    "SUBSYSTEM_STATUS_CHANGED",
                    history.getCreateDatetime(),
                    mapToHistoryUser(history.getChangedBy()),
                    subsystemInfo,
                    statusType,
                    history.getOldStatus(),
                    history.getNewStatus()
            );

            events.add(event);
        }

        return events;
    }

    /**
     * Получить информацию о файлах по их ID
     */
    private List<AttachmentInfo> getAttachmentInfos(Set<Long> attachmentIds) {
        if (attachmentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return attachmentRepository.findAllById(attachmentIds)
                .stream()
                .map(attachment -> AttachmentInfo.builder()
                        .id(attachment.getId())
                        .originalFilename(attachment.getOriginalFilename())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Получить информацию о подсистемах по их ID (affected subsystem ID)
     */
    private List<ru.c21501.rfcservice.dto.response.history.SubsystemInfo> getSubsystemInfos(Set<Long> affectedSubsystemIds) {
        if (affectedSubsystemIds.isEmpty()) {
            return Collections.emptyList();
        }

        return affectedSubsystemRepository.findAllById(affectedSubsystemIds)
                .stream()
                .map(affected -> ru.c21501.rfcservice.dto.response.history.SubsystemInfo.builder()
                        .id(affected.getId())
                        .subsystemId(affected.getSubsystem().getId())
                        .subsystemName(affected.getSubsystem().getName())
                        .systemName(affected.getSubsystem().getSystem().getName())
                        .executorId(affected.getExecutor().getId())
                        .executorName(affected.getExecutor().getFirstName() + " " + affected.getExecutor().getLastName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Маппинг UserEntity -> HistoryUser
     */
    private HistoryUser mapToHistoryUser(UserEntity user) {
        HistoryUser historyUser = new HistoryUser();
        historyUser.setId(user.getId());
        historyUser.setName(user.getFirstName() + " " + user.getLastName());
        return historyUser;
    }

    /**
     * Построить ответ с пагинацией
     */
    private RfcHistoryResponse buildHistoryResponse(List<RfcHistoryEvent> events,
                                                    int totalElements,
                                                    Pageable pageable) {
        RfcHistoryResponse response = new RfcHistoryResponse();
        response.setContent(events);
        response.setTotalElements((long) totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / pageable.getPageSize()));
        response.setSize(pageable.getPageSize());
        response.setNumber(pageable.getPageNumber());
        response.setFirst(pageable.getPageNumber() == 0);
        response.setLast(pageable.getPageNumber() >= response.getTotalPages() - 1);

        return response;
    }
}
