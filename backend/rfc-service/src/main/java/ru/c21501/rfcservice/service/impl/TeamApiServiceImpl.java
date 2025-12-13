package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.TeamMapper;
import ru.c21501.rfcservice.model.entity.TeamEntity;
import ru.c21501.rfcservice.openapi.model.TeamPageResponse;
import ru.c21501.rfcservice.openapi.model.TeamRequest;
import ru.c21501.rfcservice.openapi.model.TeamResponse;
import ru.c21501.rfcservice.service.TeamApiService;
import ru.c21501.rfcservice.service.TeamService;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с командами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamApiServiceImpl implements TeamApiService {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        log.info("API: Creating team with name: {}", request.getName());

        // Прямой маппинг: TeamRequest -> TeamEntity
        TeamEntity teamEntity = teamMapper.toEntity(request);

        // Вызов бизнес-логики с memberIds
        TeamEntity createdTeam = teamService.createTeam(
                teamEntity,
                new HashSet<>(request.getMemberIds())
        );

        // Обратный маппинг: TeamEntity -> TeamResponse
        TeamResponse response = teamMapper.toResponse(createdTeam);

        log.info("API: Team created successfully with ID: {}", response.getId());
        return response;
    }

    @Override
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        log.info("API: Updating team with ID: {}", id);

        // Прямой маппинг: TeamRequest -> TeamEntity
        TeamEntity teamEntity = teamMapper.toEntity(request);

        // Вызов бизнес-логики
        TeamEntity updatedTeam = teamService.updateTeam(
                id,
                teamEntity,
                new HashSet<>(request.getMemberIds())
        );

        // Обратный маппинг: TeamEntity -> TeamResponse
        TeamResponse response = teamMapper.toResponse(updatedTeam);

        log.info("API: Team updated successfully: {}", response.getId());
        return response;
    }

    @Override
    public TeamResponse getTeamById(Long id) {
        log.info("API: Getting team by ID: {}", id);

        // Вызов бизнес-логики
        TeamEntity team = teamService.getTeamById(id);

        // Обратный маппинг: TeamEntity -> TeamResponse
        TeamResponse response = teamMapper.toResponse(team);

        log.info("API: Team retrieved successfully: {}", response.getId());
        return response;
    }

    @Override
    public void deleteTeam(Long id) {
        log.info("API: Deleting team with ID: {}", id);

        // Вызов бизнес-логики
        teamService.deleteTeam(id);

        log.info("API: Team deleted successfully: {}", id);
    }

    @Override
    public TeamPageResponse getTeams(Integer page, Integer size, String searchString) {
        log.info("API: Getting teams - page: {}, size: {}, searchString: {}", page, size, searchString);

        // Устанавливаем значения по умолчанию
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Вызов бизнес-логики
        Page<TeamEntity> teamsPage = teamService.getTeams(searchString, pageable);

        // Маппинг в ответ
        TeamPageResponse response = new TeamPageResponse();
        response.setContent(teamsPage.getContent().stream()
                .map(teamMapper::toResponse)
                .collect(Collectors.toList()));
        response.setNumber(teamsPage.getNumber());
        response.setSize(teamsPage.getSize());
        response.setTotalElements(teamsPage.getTotalElements());
        response.setTotalPages(teamsPage.getTotalPages());
        response.setFirst(teamsPage.isFirst());
        response.setLast(teamsPage.isLast());

        log.info("API: Retrieved {} teams out of {} total", teamsPage.getNumberOfElements(), teamsPage.getTotalElements());
        return response;
    }
}
