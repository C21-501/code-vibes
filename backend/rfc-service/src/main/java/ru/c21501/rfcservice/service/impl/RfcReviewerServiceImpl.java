package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcReviewer;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.repository.RfcReviewerRepository;
import ru.c21501.rfcservice.service.RfcReviewerService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с согласующими RFC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RfcReviewerServiceImpl implements RfcReviewerService {

    private final RfcReviewerRepository rfcReviewerRepository;

    @Override
    @Transactional
    public RfcReviewer createRfcReviewer(RfcReviewer rfcReviewer) {
        log.debug("Создание согласующего RFC для RFC: {} и команды: {}",
                rfcReviewer.getRfc().getId(), rfcReviewer.getTeam().getId());
        RfcReviewer savedRfcReviewer = rfcReviewerRepository.save(rfcReviewer);
        log.info("Создан согласующий RFC с ID: {}", savedRfcReviewer.getId());
        return savedRfcReviewer;
    }

    @Override
    @Transactional
    public RfcReviewer updateRfcReviewer(RfcReviewer rfcReviewer) {
        log.debug("Обновление согласующего RFC с ID: {}", rfcReviewer.getId());
        RfcReviewer updatedRfcReviewer = rfcReviewerRepository.save(rfcReviewer);
        log.info("Обновлен согласующий RFC с ID: {}", updatedRfcReviewer.getId());
        return updatedRfcReviewer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RfcReviewer> findById(UUID id) {
        log.debug("Поиск согласующего RFC по ID: {}", id);
        return rfcReviewerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcReviewer> findByRfc(Rfc rfc) {
        log.debug("Поиск согласующих по RFC: {}", rfc.getId());
        return rfcReviewerRepository.findByRfc(rfc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcReviewer> findByRfcId(UUID rfcId) {
        log.debug("Поиск согласующих по ID RFC: {}", rfcId);
        return rfcReviewerRepository.findByRfcId(rfcId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcReviewer> findByTeam(Team team) {
        log.debug("Поиск согласующих по команде: {}", team.getId());
        return rfcReviewerRepository.findByTeam(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcReviewer> findByTeamId(UUID teamId) {
        log.debug("Поиск согласующих по ID команды: {}", teamId);
        return rfcReviewerRepository.findByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RfcReviewer> findByRfcAndTeam(Rfc rfc, Team team) {
        log.debug("Поиск согласующего по RFC: {} и команде: {}", rfc.getId(), team.getId());
        return rfcReviewerRepository.findByRfcAndTeam(rfc, team);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RfcReviewer> findByRfcIdAndTeamId(UUID rfcId, UUID teamId) {
        log.debug("Поиск согласующего по ID RFC: {} и ID команды: {}", rfcId, teamId);
        return rfcReviewerRepository.findByRfcIdAndTeamId(rfcId, teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfcReviewer> findAll() {
        log.debug("Получение всех согласующих RFC");
        return rfcReviewerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования согласующего RFC по ID: {}", id);
        return rfcReviewerRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRfcAndTeam(Rfc rfc, Team team) {
        log.debug("Проверка существования согласующего для RFC: {} и команды: {}", rfc.getId(), team.getId());
        return rfcReviewerRepository.existsByRfcAndTeam(rfc, team);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRfcIdAndTeamId(UUID rfcId, UUID teamId) {
        log.debug("Проверка существования согласующего по ID RFC: {} и ID команды: {}", rfcId, teamId);
        return rfcReviewerRepository.existsByRfcIdAndTeamId(rfcId, teamId);
    }
}
