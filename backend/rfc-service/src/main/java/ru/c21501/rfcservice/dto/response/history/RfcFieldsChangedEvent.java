package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.c21501.rfcservice.openapi.model.HistoryUser;
import ru.c21501.rfcservice.openapi.model.RfcHistoryEvent;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Событие изменения полей RFC
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RfcFieldsChangedEvent extends RfcHistoryEvent {

    public enum OperationEnum {
        CREATE, UPDATE
    }

    @JsonProperty("operation")
    private OperationEnum operation;

    @JsonProperty("changes")
    private Map<String, FieldChange> changes;

    public RfcFieldsChangedEvent(String eventType, OffsetDateTime timestamp, HistoryUser changedBy,
                                  OperationEnum operation, Map<String, FieldChange> changes) {
        super(eventType, timestamp, changedBy);
        this.operation = operation;
        this.changes = changes;
    }
}
