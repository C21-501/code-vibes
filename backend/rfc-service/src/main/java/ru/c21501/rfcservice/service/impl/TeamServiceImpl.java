package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.repository.TeamRepository;
import ru.c21501.rfcservice.service.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с командами
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {
    
    private final TeamRepository teamRepository;
    
    @Override
    @Transactional
    public Team createTeam(Team team) {
        log.debug("Создание команды: {}", team.getName());
        Team savedTeam = teamRepository.save(team);
        log.info("Создана команда с ID: {}", savedTeam.getId());
        return savedTeam;
    }
    
    @Override
    @Transactional
    public Team updateTeam(Team team) {
        log.debug("Обновление команды с ID: {}", team.getId());
        Team updatedTeam = teamRepository.save(team);
        log.info("Обновлена команда с ID: {}", updatedTeam.getId());
        return updatedTeam;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Team> findById(UUID id) {
        log.debug("Поиск команды по ID: {}", id);
        return teamRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Team> findByName(String name) {
        log.debug("Поиск команды по названию: {}", name);
        return teamRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Team> findByLeader(User leader) {
        log.debug("Поиск команд по руководителю: {}", leader.getId());
        return teamRepository.findByLeader(leader);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Team> findByLeaderId(UUID leaderId) {
        log.debug("Поиск команд по ID руководителя: {}", leaderId);
        return teamRepository.findByLeaderId(leaderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Team> findAll() {
        log.debug("Получение всех команд");
        return teamRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования команды по ID: {}", id);
        return teamRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.debug("Проверка существования команды по названию: {}", name);
        return teamRepository.existsByName(name);
    }
}
