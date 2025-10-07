package ru.c21501.rfcservice.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.mapper.TeamMapper;
import ru.c21501.rfcservice.openapi.api.TeamsApi;
import ru.c21501.rfcservice.openapi.model.CreateTeamRequest;
import ru.c21501.rfcservice.openapi.model.TeamPageResponse;
import ru.c21501.rfcservice.openapi.model.TeamResponse;
import ru.c21501.rfcservice.openapi.model.UpdateTeamRequest;
import ru.c21501.rfcservice.service.TeamService;
import ru.c21501.rfcservice.service.UserService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamController implements TeamsApi {

    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final UserService userService;

    @Override
    public TeamPageResponse teamsGet(@NotNull Integer page,
                                     @NotNull Integer size,
                                     @Nullable String name) {
        return null;
    }

    @Override
    public void teamsIdDelete(@NotNull UUID id) {

    }

    @Override
    public TeamResponse teamsIdGet(@NotNull UUID id) {
        return null;
    }

    @Override
    public TeamResponse teamsIdPut(@NotNull UUID id,
                                   @NotNull UpdateTeamRequest updateTeamRequest) {
        return null;
    }

    @Override
    public TeamResponse teamsPost(@NotNull CreateTeamRequest createTeamRequest) {
        return null;
    }
}
