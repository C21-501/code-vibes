package ru.c21501.rfcservice.dto.response.history;

import lombok.Getter;
import lombok.Setter;
import ru.c21501.rfcservice.openapi.model.HistoryUser;
import ru.c21501.rfcservice.openapi.model.RfcHistoryEvent;

import java.time.OffsetDateTime;

/**
 * Событие изменения статуса RFC (через Planka или систему)
 */
@Getter
@Setter
public class RfcStatusChangedEvent extends RfcHistoryEvent {
    
    private String oldStatus;
    private String newStatus;
    private SourceEnum source;
    private String comment;
    
    public enum SourceEnum {
        PLANKA,
        SYSTEM,
        USER
    }
    
    public RfcStatusChangedEvent(String eventType, 
                                  OffsetDateTime timestamp, 
                                  HistoryUser changedBy,
                                  String oldStatus,
                                  String newStatus,
                                  SourceEnum source,
                                  String comment) {
        super();
        this.setEventType(eventType);
        this.setTimestamp(timestamp);
        this.setChangedBy(changedBy);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.source = source;
        this.comment = comment;
    }
}


