package com.software.knowledgehub.search.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIndexRebuildMessage {

    private String taskId;
    private OffsetDateTime requestedAt;
}
