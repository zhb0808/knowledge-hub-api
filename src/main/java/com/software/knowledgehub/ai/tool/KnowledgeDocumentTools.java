package com.software.knowledgehub.ai.tool;

import com.software.knowledgehub.ai.vo.AiPublishedDocumentToolVO;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeDocumentTools {

    private final KbDocumentRepository documentRepository;

    @Tool(description = "查询企业知识库中最近更新的已发布文档。当用户询问最新文档、最近更新的文档时使用此工具。")
    public List<AiPublishedDocumentToolVO> queryLatestPublishedDocuments(
            @ToolParam(description = "需要查询的文档数量，取值范围为1到5") Integer limit) {
        int queryLimit = limit == null ? 3 : Math.max(1, Math.min(limit, 5));
        log.info("AI工具开始查询最近更新的已发布文档，limit={}", queryLimit);

        // 查询最近更新的已发布文档，并同时加载所属知识库。
        List<KbDocument> documents = documentRepository.findByStatus(
                "PUBLISHED",
                PageRequest.of(
                        0,
                        queryLimit,
                        Sort.by(Sort.Direction.DESC, "updatedTime")
                )
        );
        // 只把模型回答需要的字段交给大模型。
        return documents.stream()
                .map(document -> new AiPublishedDocumentToolVO(
                        document.getId(),
                        document.getTitle(),
                        document.getSummary(),
                        document.getKnowledgeBase().getName(),
                        document.getUpdatedTime()
                ))
                .toList();
    }
}
