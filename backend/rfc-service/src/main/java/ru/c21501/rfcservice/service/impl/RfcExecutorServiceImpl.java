package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcExecutor;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.enums.ConfirmationStatus;
import ru.c21501.rfcservice.repository.RfcExecutorRepository;
import ru.c21501.rfcservice.service.RfcExecutorService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с исполнителями RFC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RfcExecutorServiceImpl implements RfcExecutorService {
    
    private final RfcExecutorRepository rfcExecutorRepository;
    
    @Override
    @Transactional
    public RfcExecutor createRfcExecutor(RfcExecutor rfcExecutor) {
        log.debug("Создание исполнителя RFC для RFC: {} и команды: {}", 
                  rfcExecutor.getRfc().getId(), rfcExecutor.getTeam().getId());
        RfcExecutor savedRfcExecutor = rfcExecutorRepository.save(rfcExecutor);
        log.info("Создан исполнитель RFC с ID: {}", savedRfcExecutor.getId());
        return savedRfcExecutor;
    }
    
    @Override
    @Transactional
    public RfcExecutor updateRfcExecutor(RfcExecutor rfcExecutor) {
        log.debug("Обновление исполнителя RFC с ID: {}", rfcExecutor.getId());
        RfcExecutor updatedRfcExecutor = rfcExecutorRepository.save(rfcExecutor);
        log.info("Обновлен исполнитель RFC с ID: {}", updatedRfcExecutor.getId());
        return updatedRfcExecutor;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RfcExecutor> findById(UUID id) {
        log.debug("Поиск исполнителя RFC по ID: {}", id);
        return rfcExecutorRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findByRfc(Rfc rfc) {
        log.debug("Поиск исполнителей по RFC: {}", rfc.getId());
        return rfcExecutorRepository.findByRfc(rfc);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findByRfcId(String rfcId) {
        log.debug("Поиск исполнителей по ID RFC: {}", rfcId);
        return rfcExecutorRepository.findByRfcId(rfcId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findByTeam(Team team) {
        log.debug("Поиск исполнителей по команде: {}", team.getId());
        return rfcExecutorRepository.findByTeam(team);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findByTeamId(UUID teamId) {
        log.debug("Поиск исполнителей по ID команды: {}", teamId);
        return rfcExecutorRepository.findByTeamId(teamId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findByConfirmationStatus(ConfirmationStatus confirmationStatus) {
        log.debug("Поиск исполнителей по статусу подтверждения: {}", confirmationStatus);
        return rfcExecutorRepository.findByConfirmationStatus(confirmationStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RfcExecutor> findByRfcAndTeam(Rfc rfc, Team team) {
        log.debug("Поиск исполнителя по RFC: {} и команде: {}", rfc.getId(), team.getId());
        return rfcExecutorRepository.findByRfcAndTeam(rfc, team);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RfcExecutor> findByRfcIdAndTeamId(String rfcId, UUID teamId) {
        log.debug("Поиск исполнителя по ID RFC: {} и ID команды: {}", rfcId, teamId);
        return rfcExecutorRepository.findByRfcIdAndTeamId(rfcId, teamId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RfcExecutor> findAll() {
        log.debug("Получение всех исполнителей RFC");
        return rfcExecutorRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования исполнителя RFC по ID: {}", id);
        return rfcExecutorRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByRfcAndTeam(Rfc rfc, Team team) {
        log.debug("Проверка существования исполнителя для RFC: {} и команды: {}", rfc.getId(), team.getId());
        return rfcExecutorRepository.existsByRfcAndTeam(rfc, team);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByRfcIdAndTeamId(String rfcId, UUID teamId) {
        log.debug("Проверка существования исполнителя по ID RFC: {} и ID команды: {}", rfcId, teamId);
        return rfcExecutorRepository.existsByRfcIdAndTeamId(rfcId, teamId);
    }
}
