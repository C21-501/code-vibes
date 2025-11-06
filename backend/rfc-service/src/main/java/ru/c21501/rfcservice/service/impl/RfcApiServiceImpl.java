package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.RfcMapper;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.RfcPageResponse;
import ru.c21501.rfcservice.openapi.model.RfcRequest;
import ru.c21501.rfcservice.openapi.model.RfcResponse;
import ru.c21501.rfcservice.resolver.RfcActionResolver;
import ru.c21501.rfcservice.service.RfcApiService;
import ru.c21501.rfcservice.service.RfcService;
import ru.c21501.rfcservice.service.SecurityContextService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcApiServiceImpl implements RfcApiService {

    private final RfcService rfcService;
    private final RfcMapper rfcMapper;
    private final SecurityContextService securityContextService;
    private final RfcActionResolver actionResolver;

    @Override
    public RfcResponse createRfc(RfcRequest request) {
        log.info("Creating RFC: {}", request.getTitle());

        UserEntity currentUser = securityContextService.getCurrentUser();
        RfcEntity createdRfc = rfcService.createRfc(request, currentUser);

        return rfcMapper.toResponse(createdRfc, currentUser, actionResolver);
    }

    @Override
    public RfcResponse updateRfc(Long id, RfcRequest request) {
        log.info("Updating RFC with ID: {}", id);

        UserEntity currentUser = securityContextService.getCurrentUser();
        RfcEntity updatedRfc = rfcService.updateRfc(id, request, currentUser);

        return rfcMapper.toResponse(updatedRfc, currentUser, actionResolver);
    }

    @Override
    public RfcResponse getRfcById(Long id) {
        log.info("Getting RFC by ID: {}", id);

        UserEntity currentUser = securityContextService.getCurrentUser();
        RfcEntity rfc = rfcService.getRfcById(id);
        return rfcMapper.toResponse(rfc, currentUser, actionResolver);
    }

    @Override
    public void deleteRfc(Long id) {
        log.info("Deleting RFC with ID: {}", id);
        rfcService.deleteRfc(id);
    }

    @Override
    public RfcPageResponse getRfcs(Integer page, Integer size, String status, String urgency, Long requesterId) {
        log.info("Getting RFCs with filters - status: {}, urgency: {}, requesterId: {}, page: {}, size: {}",
                status, urgency, requesterId, page, size);

        // Получаем текущего пользователя для резолва actions
        UserEntity currentUser = securityContextService.getCurrentUser();

        // Создаем параметры пагинации
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20);

        // Получаем страницу с RFC
        Page<RfcEntity> rfcPage = rfcService.getRfcs(status, urgency, requesterId, pageable);

        // Конвертируем сущности в DTO с учетом текущего пользователя
        List<RfcResponse> rfcResponses = rfcPage.getContent().stream()
                .map(rfc -> rfcMapper.toResponse(rfc, currentUser, actionResolver))
                .collect(Collectors.toList());

        // Создаем ответ
        return new RfcPageResponse(
                rfcPage.getTotalElements(),
                rfcPage.getTotalPages(),
                rfcPage.getSize(),
                rfcPage.getNumber(),
                rfcPage.isFirst(),
                rfcPage.isLast(),
                rfcResponses
        );
    }
}