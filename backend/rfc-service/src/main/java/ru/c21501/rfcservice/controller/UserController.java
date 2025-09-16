package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.request.CreateUserRequest;
import ru.c21501.rfcservice.dto.request.UpdateUserRequest;
import ru.c21501.rfcservice.dto.response.UserResponse;
import ru.c21501.rfcservice.mapper.UserMapper;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    
    /**
     * Получить список всех пользователей
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Getting all users");
        
        List<User> users = userService.findAll();
        List<UserResponse> response = users.stream()
                .map(userMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить пользователя по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Getting user by id: {}", id);
        
        return userService.findById(id)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получить пользователя по имени пользователя
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        
        return userService.findByUsername(username)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Получить пользователя по email
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        
        return userService.findByEmail(email)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Создать нового пользователя
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user: {}", request.getUsername());
        
        User user = User.builder()
                .keycloakId(request.getKeycloakId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        
        User savedUser = userService.createUser(user);
        UserResponse response = userMapper.toResponse(savedUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Обновить пользователя
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        User user = User.builder()
                .id(id)
                .keycloakId(request.getKeycloakId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        
        User updatedUser = userService.updateUser(user);
        UserResponse response = userMapper.toResponse(updatedUser);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить пользователя
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
