package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.exception.TeamAlreadyExistsException;
import ru.c21501.rfcservice.model.entity.TeamEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.repository.TeamRepository;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.TeamService;

import java.util.HashSet;
import java.util.Set;

/**
 * Реализация сервиса для работы с командами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TeamEntity createTeam(TeamEntity teamEntity, Set<Long> memberIds) {
        log.info("Creating team: {}", teamEntity.getName());

        // Проверяем, что команда с таким названием еще не существует
        if (teamRepository.existsByName(teamEntity.getName())) {
            throw new TeamAlreadyExistsException(
                    String.format("Team with name '%s' already exists", teamEntity.getName())
            );
        }

        // Проверяем и загружаем пользователей-членов команды
        Set<UserEntity> members = loadMembers(memberIds);
        teamEntity.setMembers(members);

        // Сохраняем команду в БД
        TeamEntity savedTeam = teamRepository.save(teamEntity);
        log.info("Team saved to database with ID: {}", savedTeam.getId());

        return savedTeam;
    }

    @Override
    @Transactional
    public TeamEntity updateTeam(Long id, TeamEntity teamEntity, Set<Long> memberIds) {
        log.info("Updating team with ID: {}", id);

        // Проверяем, что команда существует
        TeamEntity existingTeam = getTeamById(id);

        // Проверяем, что новое название не занято другой командой
        if (!existingTeam.getName().equals(teamEntity.getName())
                && teamRepository.existsByName(teamEntity.getName())) {
            throw new TeamAlreadyExistsException(
                    String.format("Team with name '%s' already exists", teamEntity.getName())
            );
        }

        // Обновляем данные
        existingTeam.setName(teamEntity.getName());
        existingTeam.setDescription(teamEntity.getDescription());

        // Обновляем список членов команды
        Set<UserEntity> members = loadMembers(memberIds);
        existingTeam.getMembers().clear();
        existingTeam.getMembers().addAll(members);

        // Сохраняем обновленную команду
        TeamEntity updatedTeam = teamRepository.save(existingTeam);
        log.info("Team updated successfully: {}", updatedTeam.getId());

        return updatedTeam;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamEntity getTeamById(Long id) {
        log.info("Getting team by ID: {}", id);
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Team with ID %d not found", id)
                ));
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        log.info("Deleting team with ID: {}", id);

        // Проверяем, что команда существует
        TeamEntity team = getTeamById(id);

        // Удаляем команду из БД (связи в team_member удалятся автоматически благодаря CASCADE)
        teamRepository.delete(team);
        log.info("Team deleted from database: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamEntity> getTeams(String searchString, Pageable pageable) {
        log.info("Getting teams with searchString: {}, page: {}, size: {}",
                searchString, pageable.getPageNumber(), pageable.getPageSize());

        Page<TeamEntity> teams;

        if (searchString != null && !searchString.trim().isEmpty()) {
            // Поиск по строке
            teams = teamRepository.searchTeams(searchString.trim(), pageable);
            log.info("Found {} teams matching search string '{}'", teams.getTotalElements(), searchString);
        } else {
            // Получить все команды
            teams = teamRepository.findAll(pageable);
            log.info("Retrieved all teams, total: {}", teams.getTotalElements());
        }

        return teams;
    }

    /**
     * Загружает пользователей по их ID
     *
     * @param memberIds список ID пользователей
     * @return набор пользователей
     */
    private Set<UserEntity> loadMembers(Set<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("Team must have at least one member");
        }

        Set<UserEntity> members = new HashSet<>();

        for (Long memberId : memberIds) {
            UserEntity user = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("User with ID %d not found", memberId)
                    ));
            members.add(user);
        }

        log.info("Loaded {} members for team", members.size());
        return members;
    }
}
