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
 * Событие изменения файлов RFC
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RfcAttachmentsChangedEvent extends RfcHistoryEvent {

    @JsonProperty("attachmentsAdded")
    private List<AttachmentInfo> attachmentsAdded;

    @JsonProperty("attachmentsRemoved")
    private List<AttachmentInfo> attachmentsRemoved;

    public RfcAttachmentsChangedEvent(String eventType, OffsetDateTime timestamp, HistoryUser changedBy,
                                       List<AttachmentInfo> attachmentsAdded,
                                       List<AttachmentInfo> attachmentsRemoved) {
        super(eventType, timestamp, changedBy);
        this.attachmentsAdded = attachmentsAdded;
        this.attachmentsRemoved = attachmentsRemoved;
    }
}
