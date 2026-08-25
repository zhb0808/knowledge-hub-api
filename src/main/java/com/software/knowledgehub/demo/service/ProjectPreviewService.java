package com.software.knowledgehub.demo.service;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.demo.dto.KnowledgeBasePreviewDTO;
import com.software.knowledgehub.demo.vo.KnowledgeBasePreviewVO;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class ProjectPreviewService {

    private static final Set<String> RESERVED_CODES = Set.of("SYSTEM", "DEFAULT");

    /**
     * 创建经过规范化处理的知识库预览数据。
     */
    public KnowledgeBasePreviewVO createKnowledgeBasePreview(KnowledgeBasePreviewDTO request) {
        // 规范化知识库编码。
        String normalizedCode = request.getCode().strip().toUpperCase(Locale.ROOT);
        // 校验系统保留编码。
        if (RESERVED_CODES.contains(normalizedCode)) {
            throw new BusinessException("该知识库编码为系统保留编码");
        }

        return new KnowledgeBasePreviewVO(
                normalizedCode,
                request.getName().strip(),
                request.getDescription()
        );
    }
}
