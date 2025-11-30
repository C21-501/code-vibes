package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.openapi.model.HistoryUser;
import ru.c21501.rfcservice.openapi.model.RfcHistoryEvent;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Событие изменения подсистем RFC
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RfcSubsystemsChangedEvent extends RfcHistoryEvent {

    @JsonProperty("subsystemsAdded")
    private List<SubsystemInfo> subsystemsAdded;

    @JsonProperty("subsystemsRemoved")
    private List<SubsystemInfo> subsystemsRemoved;

    public RfcSubsystemsChangedEvent(String eventType, OffsetDateTime timestamp, HistoryUser changedBy,
                                      List<SubsystemInfo> subsystemsAdded,
                                      List<SubsystemInfo> subsystemsRemoved) {
        super(eventType, timestamp, changedBy);
        this.subsystemsAdded = subsystemsAdded;
        this.subsystemsRemoved = subsystemsRemoved;
    }
}
