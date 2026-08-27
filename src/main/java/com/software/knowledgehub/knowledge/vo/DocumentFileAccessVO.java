package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFileAccessVO {

    private String originalName;
    private String url;
    private OffsetDateTime expiresAt;
}
