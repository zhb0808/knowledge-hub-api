package com.software.knowledgehub.knowledge.service;

import com.software.knowledgehub.knowledge.dto.BatchUpdateDocumentStatusDTO;
import com.software.knowledgehub.knowledge.dto.CreateDocumentDTO;
import com.software.knowledgehub.knowledge.dto.DocumentQueryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateDocumentDTO;
import com.software.knowledgehub.knowledge.vo.DocumentListVO;
import com.software.knowledgehub.knowledge.vo.DocumentVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface DocumentService {

    /**
     * 创建文档及其标签关系。
     */
    DocumentVO createDocument(CreateDocumentDTO request);

    /**
     * 查询文档详情。
     */
    DocumentVO getDocument(Long id);

    /**
     * 分页查询知识库中的文档。
     */
    Page<DocumentListVO> listDocuments(DocumentQueryDTO request, Pageable pageable);

    /**
     * 批量修改知识库内的文档状态。
     */
    int batchUpdateStatus(BatchUpdateDocumentStatusDTO request);

    /**
     * 修改文档内容及其关联关系。
     */
    void updateDocument(Long id, UpdateDocumentDTO request);

    /**
     * 删除文档及其标签关系。
     */
    void deleteDocument(Long id);
}
