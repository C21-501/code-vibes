package ru.c21501.rfcservice.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.model.entity.*;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.openapi.model.*;
import ru.c21501.rfcservice.repository.*;
import ru.c21501.rfcservice.service.RfcService;
import ru.c21501.rfcservice.specification.RfcSpecification;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcServiceImpl implements RfcService {

    private final RfcRepository rfcRepository;
    private final RfcHistoryRepository rfcHistoryRepository;
    private final RfcAffectedSubsystemRepository rfcAffectedSubsystemRepository;
    private final RfcAffectedSubsystemHistoryRepository rfcAffectedSubsystemHistoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final SubsystemRepository subsystemRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RfcEntity createRfc(RfcRequest request, UserEntity requester) {

        log.info("Creating RFC: title={}, requester={}", request.getTitle(), requester.getUsername());
        //throw new RuntimeException("Тестовая ошибка: сервер временно недоступен. Попробуйте позже.");
        // 1. Проверяем и обрабатываем attachments
        List<AttachmentEntity> attachments = new ArrayList<>();
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            attachments = processAttachments(request.getAttachmentIds(), null);
        }

        // 2. Создаем RFC entity
        RfcEntity rfc = RfcEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .implementationDate(request.getImplementationDate())
                .urgency(request.getUrgency())
                .status(RfcStatus.NEW)
                .requester(requester)
                .build();

        // Сохраняем RFC, чтобы получить ID
        rfc = rfcRepository.save(rfc);

        // Принудительно сбрасываем изменения в БД для получения ID
        entityManager.flush();

        // 3. Привязываем attachments к RFC
        if (!attachments.isEmpty()) {
            for (AttachmentEntity attachment : attachments) {
                attachment.setRfc(rfc);
            }
            attachmentRepository.saveAll(attachments);
        }

        // 4. Создаем affected subsystems
        List<RfcAffectedSubsystemEntity> affectedSubsystems = createAffectedSubsystems(
                rfc,
                request.getAffectedSystems(),
                requester
        );
        rfc.getAffectedSubsystems().addAll(affectedSubsystems);

        // 5. Сохраняем все изменения
        entityManager.flush();

        // 6. Создаем историю RFC (operation = CREATE)
        createRfcHistory(rfc, HistoryOperationType.CREATE, requester, request.getAttachmentIds());

        // Принудительно обновляем entity из БД для получения актуального состояния
        entityManager.flush();
        entityManager.refresh(rfc);

        log.info("RFC created successfully: id={}", rfc.getId());
        return rfc;
    }

    @Override
    @Transactional
    public RfcEntity updateRfc(Long id, RfcRequest request, UserEntity updatedBy) {
        log.info("Updating RFC: id={}, updatedBy={}", id, updatedBy.getUsername());

        // 1. Получаем существующий RFC
        RfcEntity rfc = getRfcById(id);

        // 2. Обновляем базовые поля
        rfc.setTitle(request.getTitle());
        rfc.setDescription(request.getDescription());
        rfc.setImplementationDate(request.getImplementationDate());
        rfc.setUrgency(request.getUrgency());

        // 3. Обрабатываем attachments
        Set<Long> currentAttachmentIds = rfc.getAttachments().stream()
                .map(AttachmentEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> newAttachmentIds = request.getAttachmentIds() != null
                ? new HashSet<>(request.getAttachmentIds())
                : new HashSet<>();

        // Находим attachments, которые нужно открепить (удалить)
        Set<Long> toRemove = new HashSet<>(currentAttachmentIds);
        toRemove.removeAll(newAttachmentIds);

        if (!toRemove.isEmpty()) {
            List<AttachmentEntity> attachmentsToDelete = attachmentRepository.findAllById(toRemove);
            rfc.getAttachments().removeAll(attachmentsToDelete);
            attachmentRepository.deleteAll(attachmentsToDelete);
            log.info("Deleted {} detached attachments", toRemove.size());
        }

        // Находим новые attachments для привязки
        Set<Long> toAdd = new HashSet<>(newAttachmentIds);
        toAdd.removeAll(currentAttachmentIds);

        if (!toAdd.isEmpty()) {
            List<AttachmentEntity> newAttachments = processAttachments(new ArrayList<>(toAdd), rfc.getId());
            rfc.getAttachments().addAll(newAttachments);
            for (AttachmentEntity attachment : newAttachments) {
                attachment.setRfc(rfc);
            }
            attachmentRepository.saveAll(newAttachments);
        }

        // 4. Обновляем affected subsystems
        updateAffectedSubsystems(rfc, request.getAffectedSystems(), updatedBy);

        // 5. Сохраняем изменения
        rfc = rfcRepository.save(rfc);
        entityManager.flush();

        // 6. Создаем историю RFC (operation = UPDATE)
        createRfcHistory(rfc, HistoryOperationType.UPDATE, updatedBy, request.getAttachmentIds());

        // Принудительно обновляем entity из БД
        entityManager.refresh(rfc);

        log.info("RFC updated successfully: id={}", rfc.getId());
        return rfc;
    }

    @Override
    @Transactional(readOnly = true)
    public RfcEntity getRfcById(Long id) {
        log.debug("Getting RFC by id: {}", id);
        return rfcRepository.findById(id)
                .filter(rfc -> rfc.getDeletedDatetime() == null)
                .orElseThrow(() -> new ResourceNotFoundException("RFC not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RfcEntity> getRfcs(String status, String urgency, Long requesterId, String title, Pageable pageable) {
        log.debug("Getting RFCs: status={}, urgency={}, requesterId={}, title={}",
                status, urgency, requesterId, title);

        Specification<RfcEntity> spec = RfcSpecification.isNotDeleted();

        if (status != null) {
            spec = spec.and(RfcSpecification.hasStatus(status));
        }
        if (urgency != null) {
            spec = spec.and(RfcSpecification.hasUrgency(urgency));
        }
        if (requesterId != null) {
            spec = spec.and(RfcSpecification.hasRequesterId(requesterId));
        }
        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and(RfcSpecification.hasTitleLike(title.trim()));
        }

        return rfcRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void deleteRfc(Long id) {
        log.info("Deleting RFC: id={}", id);

        RfcEntity rfc = getRfcById(id);

        // Soft delete - устанавливаем дату удаления
        rfc.setDeletedDatetime(OffsetDateTime.now());
        rfcRepository.save(rfc);

        entityManager.flush();

        // Создаем историю RFC (operation = DELETE)
        createRfcHistory(rfc, HistoryOperationType.DELETE, getCurrentUser(rfc), null);

        log.info("RFC deleted successfully: id={}", id);
    }

    /**
     * Проверяет и обрабатывает attachments
     *
     * @param attachmentIds список ID attachments
     * @param currentRfcId  ID текущего RFC (для обновления, null для создания)
     * @return список проверенных attachments
     */
    private List<AttachmentEntity> processAttachments(List<Long> attachmentIds, Long currentRfcId) {
        List<AttachmentEntity> attachments = attachmentRepository.findAllById(attachmentIds);

        if (attachments.size() != attachmentIds.size()) {
            Set<Long> foundIds = attachments.stream()
                    .map(AttachmentEntity::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = new HashSet<>(attachmentIds);
            missingIds.removeAll(foundIds);
            throw new ResourceNotFoundException("Attachments not found with ids: " + missingIds);
        }

        // Проверяем, что файлы еще не привязаны к другим RFC
        for (AttachmentEntity attachment : attachments) {
            if (attachment.getRfc() != null) {
                // Если это обновление и файл уже привязан к этому RFC - OK
                if (attachment.getRfc().getId().equals(currentRfcId)) {
                    continue;
                }
                throw new IllegalArgumentException(
                        String.format("Attachment with id %d is already assigned to RFC %d",
                                attachment.getId(),
                                attachment.getRfc().getId())
                );
            }
        }

        return attachments;
    }

    /**
     * Создает affected subsystems для RFC
     *
     * @param rfc             RFC entity
     * @param affectedSystems данные о затронутых системах
     * @param changedBy       пользователь, создающий записи
     * @return список созданных affected subsystems
     */
    private List<RfcAffectedSubsystemEntity> createAffectedSubsystems(
            RfcEntity rfc,
            List<AffectedSystemRequest> affectedSystems,
            UserEntity changedBy
    ) {
        List<RfcAffectedSubsystemEntity> result = new ArrayList<>();

        for (AffectedSystemRequest systemRequest : affectedSystems) {
            for (AffectedSubsystemRequest subsystemRequest : systemRequest.getAffectedSubsystems()) {
                // Получаем subsystem
                SubsystemEntity subsystem = subsystemRepository.findById(subsystemRequest.getSubsystemId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Subsystem not found with id: " + subsystemRequest.getSubsystemId()));

                // Получаем executor
                UserEntity executor = userRepository.findById(subsystemRequest.getExecutorId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with id: " + subsystemRequest.getExecutorId()));

                // Создаем affected subsystem
                RfcAffectedSubsystemEntity affectedSubsystem = RfcAffectedSubsystemEntity.builder()
                        .rfc(rfc)
                        .subsystem(subsystem)
                        .executor(executor)
                        .confirmationStatus(ConfirmationStatus.PENDING)
                        .executionStatus(ExecutionStatus.PENDING)
                        .build();

                affectedSubsystem = rfcAffectedSubsystemRepository.save(affectedSubsystem);
                result.add(affectedSubsystem);
            }
        }

        // Создаем историю для affected subsystems (operation = CREATE для обоих статусов)
        createAffectedSubsystemHistories(result, changedBy);

        return result;
    }

    /**
     * Обновляет affected subsystems для RFC
     *
     * @param rfc             RFC entity
     * @param affectedSystems новые данные о затронутых системах
     */
    private void updateAffectedSubsystems(RfcEntity rfc, List<AffectedSystemRequest> affectedSystems, UserEntity changedBy) {
        // Собираем новые комбинации subsystemId-executorId
        Set<SubsystemExecutorPair> newPairs = affectedSystems.stream()
                .flatMap(system -> system.getAffectedSubsystems().stream())
                .map(subsystem -> new SubsystemExecutorPair(
                        subsystem.getSubsystemId(),
                        subsystem.getExecutorId()))
                .collect(Collectors.toSet());

        // Получаем текущие affected subsystems
        List<RfcAffectedSubsystemEntity> currentSubsystems = new ArrayList<>(rfc.getAffectedSubsystems());

        // Находим subsystems для удаления
        List<RfcAffectedSubsystemEntity> toRemove = currentSubsystems.stream()
                .filter(as -> !newPairs.contains(new SubsystemExecutorPair(
                        as.getSubsystem().getId(),
                        as.getExecutor().getId())))
                .collect(Collectors.toList());

        if (!toRemove.isEmpty()) {
            // Удаляем связанные истории
            List<Long> toRemoveIds = toRemove.stream()
                    .map(RfcAffectedSubsystemEntity::getId)
                    .collect(Collectors.toList());

            // Удаляем истории subsystems
            rfcAffectedSubsystemHistoryRepository.deleteAll(
                    rfcAffectedSubsystemHistoryRepository.findAll().stream()
                            .filter(h -> toRemoveIds.contains(h.getRfcAffectedSubsystemId()))
                            .collect(Collectors.toList())
            );

            // Удаляем affected subsystems
            rfcAffectedSubsystemRepository.deleteAll(toRemove);
            rfc.getAffectedSubsystems().removeAll(toRemove);
        }

        // Находим существующие пары
        Set<SubsystemExecutorPair> currentPairs = currentSubsystems.stream()
                .map(as -> new SubsystemExecutorPair(
                        as.getSubsystem().getId(),
                        as.getExecutor().getId()))
                .collect(Collectors.toSet());

        // Добавляем новые subsystems
        Set<SubsystemExecutorPair> toAdd = new HashSet<>(newPairs);
        toAdd.removeAll(currentPairs);

        List<RfcAffectedSubsystemEntity> addedEntities = new ArrayList<>();
        if (!toAdd.isEmpty()) {
            for (SubsystemExecutorPair pair : toAdd) {
                SubsystemEntity subsystem = subsystemRepository.findById(pair.subsystemId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Subsystem not found with id: " + pair.subsystemId()));

                UserEntity executor = userRepository.findById(pair.executorId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with id: " + pair.executorId()));

                RfcAffectedSubsystemEntity affectedSubsystem = RfcAffectedSubsystemEntity.builder()
                        .rfc(rfc)
                        .subsystem(subsystem)
                        .executor(executor)
                        .confirmationStatus(ConfirmationStatus.PENDING)
                        .executionStatus(ExecutionStatus.PENDING)
                        .build();

                affectedSubsystem = rfcAffectedSubsystemRepository.save(affectedSubsystem);
                rfc.getAffectedSubsystems().add(affectedSubsystem);
                addedEntities.add(affectedSubsystem);

            }
        }
        createAffectedSubsystemHistories(addedEntities, changedBy);

    }

    /**
     * Создает запись в истории RFC
     *
     * @param rfc           RFC entity
     * @param operation     тип операции
     * @param changedBy     пользователь, выполнивший изменение
     * @param attachmentIds список ID attachments
     */
    private void createRfcHistory(RfcEntity rfc, HistoryOperationType operation,
                                   UserEntity changedBy, List<Long> attachmentIds) {
        Set<Long> attachmentIdSet = attachmentIds != null && !attachmentIds.isEmpty()
                ? new HashSet<>(attachmentIds)
                : rfc.getAttachments().stream()
                        .map(AttachmentEntity::getId)
                        .collect(Collectors.toSet());

        Set<Long> affectedSubsystemIds = rfc.getAffectedSubsystems().stream()
                .map(RfcAffectedSubsystemEntity::getId)
                .collect(Collectors.toSet());

        RfcHistoryEntity history = RfcHistoryEntity.builder()
                .rfcId(rfc.getId())
                .operation(operation)
                .changedBy(changedBy)
                .title(rfc.getTitle())
                .description(rfc.getDescription())
                .implementationDate(rfc.getImplementationDate())
                .urgency(rfc.getUrgency())
                .status(rfc.getStatus())
                .requester(rfc.getRequester())
                .attachmentIds(attachmentIdSet)
                .affectedSubsystems(affectedSubsystemIds)
                .build();

        rfcHistoryRepository.save(history);
    }

    /**
     * Создает записи в истории для affected subsystems
     *
     * @param affectedSubsystems список affected subsystems
     * @param changedBy          пользователь, выполнивший изменение
     */
    private void createAffectedSubsystemHistories(
            List<RfcAffectedSubsystemEntity> affectedSubsystems,
            UserEntity changedBy
    ) {
        for (RfcAffectedSubsystemEntity affectedSubsystem : affectedSubsystems) {
            // История для confirmation status
            RfcAffectedSubsystemHistoryEntity confirmationHistory = RfcAffectedSubsystemHistoryEntity.builder()
                    .rfcAffectedSubsystemId(affectedSubsystem.getId())
                    .operation(HistoryOperationType.CREATE)
                    .statusType("CONFIRMATION")
                    .oldStatus(null)
                    .newStatus(affectedSubsystem.getConfirmationStatus().name())
                    .changedBy(changedBy)
                    .build();

            // История для execution status
            RfcAffectedSubsystemHistoryEntity executionHistory = RfcAffectedSubsystemHistoryEntity.builder()
                    .rfcAffectedSubsystemId(affectedSubsystem.getId())
                    .operation(HistoryOperationType.CREATE)
                    .statusType("EXECUTION")
                    .oldStatus(null)
                    .newStatus(affectedSubsystem.getExecutionStatus().name())
                    .changedBy(changedBy)
                    .build();

            rfcAffectedSubsystemHistoryRepository.save(confirmationHistory);
            rfcAffectedSubsystemHistoryRepository.save(executionHistory);
        }
    }

    /**
     * Получает текущего пользователя (для операции удаления используем requester)
     *
     * @param rfc RFC entity
     * @return пользователь
     */
    private UserEntity getCurrentUser(RfcEntity rfc) {
        // В реальном приложении здесь должна быть логика получения текущего пользователя из SecurityContext
        // Для простоты используем requester
        return rfc.getRequester();
    }

    /**
     * Вспомогательный класс для хранения пары subsystemId-executorId
     */
    private record SubsystemExecutorPair(Long subsystemId, Long executorId) {
    }
}