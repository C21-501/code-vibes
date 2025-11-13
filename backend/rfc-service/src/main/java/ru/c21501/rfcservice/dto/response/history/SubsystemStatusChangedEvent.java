package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.openapi.model.HistoryUser;
import ru.c21501.rfcservice.openapi.model.RfcHistoryEvent;

import java.time.OffsetDateTime;

/**
 * Событие изменения статуса подсистемы
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SubsystemStatusChangedEvent extends RfcHistoryEvent {

    public enum StatusTypeEnum {
        CONFIRMATION, EXECUTION
    }

    @JsonProperty("subsystem")
    private SubsystemInfo subsystem;

    @JsonProperty("statusType")
    private StatusTypeEnum statusType;

    @JsonProperty("oldStatus")
    private String oldStatus;

    @JsonProperty("newStatus")
    private String newStatus;

    public SubsystemStatusChangedEvent(String eventType, OffsetDateTime timestamp, HistoryUser changedBy,
                                        SubsystemInfo subsystem, StatusTypeEnum statusType,
                                        String oldStatus, String newStatus) {
        super(eventType, timestamp, changedBy);
        this.subsystem = subsystem;
        this.statusType = statusType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
