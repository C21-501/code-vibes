package ru.c21501.rfcservice.dto.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Краткая информация о файле
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentInfo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("originalFilename")
    private String originalFilename;
}
