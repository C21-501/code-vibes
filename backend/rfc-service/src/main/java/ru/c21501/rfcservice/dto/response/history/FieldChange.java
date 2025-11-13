package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Изменение поля
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldChange {

    @JsonProperty("oldValue")
    private Object oldValue;

    @JsonProperty("newValue")
    private Object newValue;
}
