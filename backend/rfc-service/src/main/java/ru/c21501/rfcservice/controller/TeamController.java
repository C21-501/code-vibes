package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.request.CreateTeamRequest;
import ru.c21501.rfcservice.dto.request.UpdateTeamRequest;
import ru.c21501.rfcservice.dto.response.TeamResponse;
import ru.c21501.rfcservice.mapper.TeamMapper;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.service.TeamService;
import ru.c21501.rfcservice.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с командами
 */
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final UserService userService;

    /**
     * Получить список всех команд
     */
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        log.info("Getting all teams");

        List<Team> teams = teamService.findAll();
        List<TeamResponse> response = teams.stream()
                .map(teamMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Получить команду по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID id) {
        log.info("Getting team by id: {}", id);

        return teamService.findById(id)
                .map(teamMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить команду по названию
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<TeamResponse> getTeamByName(@PathVariable String name) {
        log.info("Getting team by name: {}", name);

        return teamService.findByName(name)
                .map(teamMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить команды по ID руководителя
     */
    @GetMapping("/by-leader/{leaderId}")
    public ResponseEntity<List<TeamResponse>> getTeamsByLeaderId(@PathVariable UUID leaderId) {
        log.info("Getting teams by leader id: {}", leaderId);

        List<Team> teams = teamService.findByLeaderId(leaderId);
        List<TeamResponse> response = teams.stream()
                .map(teamMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Создать новую команду
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        log.info("Creating team: {}", request.getName());

        User leader = userService.findById(request.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Руководитель не найден"));

        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .leader(leader)
                .build();

        Team savedTeam = teamService.createTeam(team);
        TeamResponse response = teamMapper.toResponse(savedTeam);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Обновить команду
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable UUID id, @Valid @RequestBody UpdateTeamRequest request) {
        log.info("Updating team with id: {}", id);

        if (!teamService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        User leader = userService.findById(request.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Руководитель не найден"));

        Team team = Team.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .leader(leader)
                .build();

        Team updatedTeam = teamService.updateTeam(team);
        TeamResponse response = teamMapper.toResponse(updatedTeam);

        return ResponseEntity.ok(response);
    }

    /**
     * Удалить команду
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        log.info("Deleting team with id: {}", id);

        if (!teamService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
