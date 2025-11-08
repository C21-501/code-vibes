package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ForbiddenException;
import ru.c21501.rfcservice.exception.NotFoundException;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.repository.RfcApprovalRepository;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.RfcApprovalService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Реализация сервиса для работы с согласованиями RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcApprovalServiceImpl implements RfcApprovalService {

    private final RfcApprovalRepository rfcApprovalRepository;
    private final RfcRepository rfcRepository;

    @Override
    @Transactional
    public RfcApprovalEntity approveRfc(Long rfcId, String comment, UserEntity currentUser) {
        log.info("Approving RFC {} by user {}", rfcId, currentUser.getId());

        // Проверка прав доступа
        validateApprovalAccess(currentUser);

        // Проверка существования RFC
        RfcEntity rfc = rfcRepository.findById(rfcId)
                .orElseThrow(() -> {
                    log.warn("RFC not found: {}", rfcId);
                    return new NotFoundException("RFC не найден");
                });

        // Создание или обновление записи согласования
        RfcApprovalEntity approval = rfcApprovalRepository
                .findByRfcIdAndApproverId(rfcId, currentUser.getId())
                .orElse(new RfcApprovalEntity());

        approval.setRfc(rfc);
        approval.setApprover(currentUser);
        approval.setIsApproved(true);
        approval.setComment(comment);

        if (approval.getId() == null) {
            approval.setCreateDatetime(OffsetDateTime.now());
        }
        approval.setUpdateDatetime(OffsetDateTime.now());

        RfcApprovalEntity saved = rfcApprovalRepository.save(approval);
        log.info("RFC {} approved by user {}", rfcId, currentUser.getId());

        return saved;
    }

    @Override
    @Transactional
    public RfcApprovalEntity unapproveRfc(Long rfcId, String comment, UserEntity currentUser) {
        log.info("Unapproving RFC {} by user {}", rfcId, currentUser.getId());

        // Проверка прав доступа
        validateApprovalAccess(currentUser);

        // Проверка существования RFC
        RfcEntity rfc = rfcRepository.findById(rfcId)
                .orElseThrow(() -> {
                    log.warn("RFC not found: {}", rfcId);
                    return new NotFoundException("RFC не найден");
                });

        // Создание или обновление записи согласования
        RfcApprovalEntity approval = rfcApprovalRepository
                .findByRfcIdAndApproverId(rfcId, currentUser.getId())
                .orElse(new RfcApprovalEntity());

        approval.setRfc(rfc);
        approval.setApprover(currentUser);
        approval.setIsApproved(false);
        approval.setComment(comment);

        if (approval.getId() == null) {
            approval.setCreateDatetime(OffsetDateTime.now());
        }
        approval.setUpdateDatetime(OffsetDateTime.now());

        RfcApprovalEntity saved = rfcApprovalRepository.save(approval);
        log.info("RFC {} unapproved by user {}", rfcId, currentUser.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcApprovalEntity> getRfcApprovals(Long rfcId) {
        log.info("Getting approvals for RFC {}", rfcId);

        // Проверка существования RFC
        if (!rfcRepository.existsById(rfcId)) {
            log.warn("RFC not found: {}", rfcId);
            throw new NotFoundException("RFC не найден");
        }

        return rfcApprovalRepository.findByRfcId(rfcId);
    }

    /**
     * Проверяет, имеет ли пользователь право согласовывать RFC
     * Правило: только RFC_APPROVER, CAB_MANAGER или ADMIN
     */
    private void validateApprovalAccess(UserEntity currentUser) {
        UserRole role = currentUser.getRole();

        if (role != UserRole.RFC_APPROVER &&
            role != UserRole.CAB_MANAGER &&
            role != UserRole.ADMIN) {

            log.warn("User {} has no access to approve RFC", currentUser.getId());
            throw new ForbiddenException("Недостаточно прав для согласования RFC");
        }
    }
}