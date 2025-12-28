package ru.c21501.rfcservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.c21501.rfcservice.dto.planka.PlankaCardRequest;
import ru.c21501.rfcservice.dto.planka.PlankaCardResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * HTTP клиент для взаимодействия с Planka API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlankaClient {

    private final RestTemplate restTemplate;

    @Value("${planka.url:http://localhost:3000}")
    private String plankaUrl;

    @Value("${planka.api-token:}")
    private String apiToken;

    /**
     * Создать карточку в Planka
     */
    public Optional<PlankaCardResponse> createCard(String listId, PlankaCardRequest request) {
        String url = plankaUrl + "/api/lists/" + listId + "/cards";
        log.info("Creating card in Planka: listId={}, name={}", listId, request.getName());

        try {
            HttpEntity<PlankaCardRequest> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Card created successfully in Planka: {}", response.getBody().getItem());
                return Optional.ofNullable(mapToCardResponse(response.getBody().getItem()));
            }
        } catch (RestClientException e) {
            log.error("Failed to create card in Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Обновить карточку в Planka
     */
    public Optional<PlankaCardResponse> updateCard(String cardId, PlankaCardRequest request) {
        String url = plankaUrl + "/api/cards/" + cardId;
        log.info("Updating card in Planka: cardId={}", cardId);

        try {
            HttpEntity<PlankaCardRequest> entity = new HttpEntity<>(request, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Card updated successfully in Planka");
                return Optional.ofNullable(mapToCardResponse(response.getBody().getItem()));
            }
        } catch (RestClientException e) {
            log.error("Failed to update card in Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Переместить карточку в другой список (изменение статуса)
     */
    public Optional<PlankaCardResponse> moveCard(String cardId, String targetListId, Double position) {
        String url = plankaUrl + "/api/cards/" + cardId;
        log.info("Moving card in Planka: cardId={}, targetListId={}", cardId, targetListId);

        try {
            Map<String, Object> body = Map.of(
                    "listId", targetListId,
                    "position", position != null ? position : 65536.0
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Card moved successfully in Planka");
                return Optional.ofNullable(mapToCardResponse(response.getBody().getItem()));
            }
        } catch (RestClientException e) {
            log.error("Failed to move card in Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Удалить карточку из Planka
     */
    public boolean deleteCard(String cardId) {
        String url = plankaUrl + "/api/cards/" + cardId;
        log.info("Deleting card from Planka: cardId={}", cardId);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Card deleted successfully from Planka");
                return true;
            }
        } catch (RestClientException e) {
            log.error("Failed to delete card from Planka: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Получить карточку по ID
     */
    public Optional<PlankaCardResponse> getCard(String cardId) {
        String url = plankaUrl + "/api/cards/" + cardId;
        log.debug("Getting card from Planka: cardId={}", cardId);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.ofNullable(mapToCardResponse(response.getBody().getItem()));
            }
        } catch (RestClientException e) {
            log.error("Failed to get card from Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Получить списки доски
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getBoardLists(String boardId) {
        String url = plankaUrl + "/api/boards/" + boardId;
        log.debug("Getting board lists from Planka: boardId={}", boardId);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object included = response.getBody().getIncluded();
                if (included instanceof Map) {
                    Map<String, Object> includedMap = (Map<String, Object>) included;
                    Object lists = includedMap.get("lists");
                    if (lists instanceof List) {
                        return (List<Map<String, Object>>) lists;
                    }
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to get board lists from Planka: {}", e.getMessage(), e);
        }
        return List.of();
    }

    /**
     * Найти ID списка по названию
     */
    public Optional<String> findListIdByName(String boardId, String listName) {
        List<Map<String, Object>> lists = getBoardLists(boardId);
        return lists.stream()
                .filter(list -> listName.equalsIgnoreCase((String) list.get("name")))
                .map(list -> (String) list.get("id"))
                .findFirst();
    }

    /**
     * Аутентификация в Planka
     */
    public Optional<String> authenticate(String emailOrUsername, String password) {
        String url = plankaUrl + "/api/access-tokens";
        log.info("Authenticating in Planka: user={}", emailOrUsername);

        try {
            Map<String, Object> body = Map.of(
                    "emailOrUsername", emailOrUsername,
                    "password", password
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = (String) response.getBody().getItem();
                log.info("Authentication successful");
                return Optional.ofNullable(token);
            }
        } catch (RestClientException e) {
            log.error("Failed to authenticate in Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Создать пользователя в Planka
     *
     * @param email    email пользователя
     * @param name     полное имя пользователя
     * @param username username пользователя
     * @param password пароль (опционально для SSO пользователей)
     * @param role     роль в Planka (admin, projectOwner, boardUser)
     * @return ID созданного пользователя
     */
    public Optional<String> createUser(String email, String name, String username, String password, String role) {
        String url = plankaUrl + "/api/users";
        log.info("Creating user in Planka: email={}, username={}, role={}", email, username, role);

        try {
            Map<String, Object> body = new java.util.HashMap<>();
            body.put("email", email);
            body.put("name", name);
            body.put("username", username);
            if (password != null && !password.isBlank()) {
                body.put("password", password);
            }
            if (role != null && !role.isBlank()) {
                body.put("role", role);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String userId = extractUserId(response.getBody().getItem());
                log.info("User created successfully in Planka: userId={}", userId);
                return Optional.ofNullable(userId);
            }
        } catch (RestClientException e) {
            log.error("Failed to create user in Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Обновить пользователя в Planka
     *
     * @param userId   ID пользователя в Planka
     * @param name     новое имя (опционально)
     * @param username новый username (опционально)
     * @param role     новая роль (опционально)
     * @return true если успешно
     */
    public boolean updateUser(String userId, String name, String username, String role) {
        String url = plankaUrl + "/api/users/" + userId;
        log.info("Updating user in Planka: userId={}", userId);

        try {
            Map<String, Object> body = new java.util.HashMap<>();
            if (name != null) {
                body.put("name", name);
            }
            if (username != null) {
                body.put("username", username);
            }
            if (role != null) {
                body.put("role", role);
            }

            if (body.isEmpty()) {
                log.debug("No fields to update for user: {}", userId);
                return true;
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("User updated successfully in Planka: userId={}", userId);
                return true;
            }
        } catch (RestClientException e) {
            log.error("Failed to update user in Planka: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Получить список всех пользователей из Planka
     *
     * @return список пользователей
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUsers() {
        String url = plankaUrl + "/api/users";
        log.debug("Getting users from Planka");

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object items = response.getBody().getItem();
                if (items instanceof List) {
                    return (List<Map<String, Object>>) items;
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to get users from Planka: {}", e.getMessage(), e);
        }
        return List.of();
    }

    /**
     * Найти пользователя по email
     *
     * @param email email пользователя
     * @return ID пользователя если найден
     */
    public Optional<String> findUserByEmail(String email) {
        List<Map<String, Object>> users = getUsers();
        return users.stream()
                .filter(user -> email.equalsIgnoreCase((String) user.get("email")))
                .map(user -> (String) user.get("id"))
                .findFirst();
    }

    /**
     * Найти пользователя по username
     *
     * @param username username пользователя
     * @return ID пользователя если найден
     */
    public Optional<String> findUserByUsername(String username) {
        List<Map<String, Object>> users = getUsers();
        return users.stream()
                .filter(user -> username.equalsIgnoreCase((String) user.get("username")))
                .map(user -> (String) user.get("id"))
                .findFirst();
    }

    /**
     * Получить последнее действие (action) для карточки - используется для определения кто переместил карточку
     * 
     * @param cardId ID карточки
     * @return информация о пользователе, который выполнил последнее действие
     */
    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> getLastCardAction(String cardId) {
        String url = plankaUrl + "/api/cards/" + cardId;
        log.debug("Getting card actions from Planka: cardId={}", cardId);

        try {
            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<PlankaApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, PlankaApiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object included = response.getBody().getIncluded();
                Object item = response.getBody().getItem();
                
                log.debug("Card API response - included keys: {}", 
                        included instanceof Map ? ((Map<?, ?>) included).keySet() : "not a map");
                
                if (included instanceof Map) {
                    Map<String, Object> includedMap = (Map<String, Object>) included;
                    
                    // Получаем actions
                    Object actions = includedMap.get("actions");
                    log.debug("Card actions: {}", actions);
                    
                    if (actions instanceof List) {
                        List<Map<String, Object>> actionsList = (List<Map<String, Object>>) actions;
                        log.debug("Found {} actions for card", actionsList.size());
                        
                        if (!actionsList.isEmpty()) {
                            // Ищем последнее действие типа moveCard или последнее любое действие
                            Map<String, Object> lastAction = null;
                            for (int i = actionsList.size() - 1; i >= 0; i--) {
                                Map<String, Object> action = actionsList.get(i);
                                String type = (String) action.get("type");
                                log.debug("Action {}: type={}, data={}", i, type, action);
                                
                                // Приоритет: moveCard
                                if ("moveCard".equals(type)) {
                                    lastAction = action;
                                    break;
                                }
                                if (lastAction == null) {
                                    lastAction = action;
                                }
                            }
                            
                            if (lastAction != null) {
                                // Получаем userId из action
                                String userId = (String) lastAction.get("userId");
                                log.debug("Action userId: {}", userId);
                                
                                if (userId != null) {
                                    // Ищем пользователя в included.users
                                    Object users = includedMap.get("users");
                                    if (users instanceof List) {
                                        List<Map<String, Object>> usersList = (List<Map<String, Object>>) users;
                                        Optional<Map<String, Object>> user = usersList.stream()
                                                .filter(u -> userId.equals(u.get("id")))
                                                .findFirst();
                                        if (user.isPresent()) {
                                            log.info("Found user from action: {}", user.get().get("username"));
                                            return user;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Fallback: ищем по creatorUserId из item карточки
                    if (item instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        String creatorUserId = (String) itemMap.get("creatorUserId");
                        log.debug("Card creatorUserId: {}", creatorUserId);
                        
                        if (creatorUserId != null) {
                            Object users = includedMap.get("users");
                            if (users instanceof List) {
                                List<Map<String, Object>> usersList = (List<Map<String, Object>>) users;
                                Optional<Map<String, Object>> user = usersList.stream()
                                        .filter(u -> creatorUserId.equals(u.get("id")))
                                        .findFirst();
                                if (user.isPresent()) {
                                    log.info("Using card creator as fallback: {}", user.get().get("username"));
                                    return user;
                                }
                            }
                        }
                    }
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to get card actions from Planka: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Маппинг роли из RFC в Planka
     *
     * @param rfcRole роль в RFC системе
     * @return роль в Planka
     */
    public static String mapRfcRoleToPlankaRole(String rfcRole) {
        if (rfcRole == null) {
            return "boardUser";
        }
        return switch (rfcRole.toUpperCase()) {
            case "ADMIN" -> "admin";
            case "CAB_MANAGER", "RFC_APPROVER" -> "projectOwner";
            default -> "boardUser";
        };
    }

    @SuppressWarnings("unchecked")
    private String extractUserId(Object item) {
        if (item instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) item;
            return (String) map.get("id");
        }
        return null;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiToken != null && !apiToken.isBlank()) {
            // Planka использует заголовок X-API-Key для API ключей
            headers.set("X-API-Key", apiToken);
        }
        return headers;
    }

    @SuppressWarnings("unchecked")
    private PlankaCardResponse mapToCardResponse(Object item) {
        if (item == null) return null;
        
        if (item instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) item;
            return PlankaCardResponse.builder()
                    .id((String) map.get("id"))
                    .name((String) map.get("name"))
                    .description((String) map.get("description"))
                    .listId((String) map.get("listId"))
                    .boardId((String) map.get("boardId"))
                    .position(map.get("position") != null ? ((Number) map.get("position")).doubleValue() : null)
                    .type((String) map.get("type"))
                    .rfcData((Map<String, Object>) map.get("rfcData"))
                    .build();
        }
        return null;
    }

    /**
     * Wrapper для ответа Planka API
     */
    @lombok.Data
    public static class PlankaApiResponse {
        private Object item;
        private Object included;
    }
}

