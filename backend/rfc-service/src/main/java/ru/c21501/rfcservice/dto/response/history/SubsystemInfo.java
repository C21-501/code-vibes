package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о подсистеме в истории
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsystemInfo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("subsystemId")
    private Long subsystemId;

    @JsonProperty("subsystemName")
    private String subsystemName;

    @JsonProperty("systemName")
    private String systemName;

    @JsonProperty("executorId")
    private Long executorId;

    @JsonProperty("executorName")
    private String executorName;
}
