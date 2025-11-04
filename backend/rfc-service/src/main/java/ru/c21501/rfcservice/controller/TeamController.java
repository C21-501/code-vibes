package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.TeamsApi;
import ru.c21501.rfcservice.openapi.model.TeamPageResponse;
import ru.c21501.rfcservice.openapi.model.TeamRequest;
import ru.c21501.rfcservice.openapi.model.TeamResponse;
import ru.c21501.rfcservice.service.TeamApiService;

/**
 * Контроллер для работы с командами
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TeamController implements TeamsApi {

    private final TeamApiService teamApiService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse createTeam(TeamRequest teamRequest) {
        log.info("POST /api/teams - Creating team: {}", teamRequest.getName());
        return teamApiService.createTeam(teamRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(Long id) {
        log.info("DELETE /api/teams/{} - Deleting team", id);
        teamApiService.deleteTeam(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamResponse getTeamById(Long id) {
        log.info("GET /api/teams/{} - Getting team by ID", id);
        return teamApiService.getTeamById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamPageResponse getTeams(Integer page, Integer size, String name) {
        log.info("GET /api/teams - Getting teams list (page: {}, size: {}, name: {})", page, size, name);
        return teamApiService.getTeams(page, size, name);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamResponse updateTeam(Long id, TeamRequest teamRequest) {
        log.info("PUT /api/teams/{} - Updating team: {}", id, teamRequest.getName());
        return teamApiService.updateTeam(id, teamRequest);
    }
}