package com.software.knowledgehub.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentIndexRebuildTaskVO {

    private String taskId;
    private String status;
}
