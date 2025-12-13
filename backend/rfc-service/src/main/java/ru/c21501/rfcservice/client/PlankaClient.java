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

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiToken != null && !apiToken.isBlank()) {
            headers.setBearerAuth(apiToken);
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

