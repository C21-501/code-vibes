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

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TeamController implements TeamsApi {

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse createTeam(TeamRequest teamRequest) {
        log.info("createTeam called with: {}", teamRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(Long id) {
        log.info("deleteTeam called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamResponse getTeamById(Long id) {
        log.info("getTeamById called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamPageResponse getTeams(Integer page, Integer size, String name) {
        log.info("getTeams called with page: {}, size: {}, name: {}", page, size, name);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public TeamResponse updateTeam(Long id, TeamRequest teamRequest) {
        log.info("updateTeam called with id: {}, teamRequest: {}", id, teamRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}