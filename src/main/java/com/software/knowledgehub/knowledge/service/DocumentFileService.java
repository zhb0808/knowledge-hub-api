package com.software.knowledgehub.knowledge.service;

import com.software.knowledgehub.knowledge.vo.DocumentFileAccessVO;
import com.software.knowledgehub.knowledge.vo.DocumentFileVO;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentFileService {

    /**
     * 为文档首次上传文件。
     */
    DocumentFileVO uploadFile(Long documentId, MultipartFile file);

    /**
     * 替换文档已关联的文件。
     */
    DocumentFileVO replaceFile(Long documentId, MultipartFile file);

    /**
     * 删除文档关联的文件。
     */
    void deleteFile(Long documentId);

    /**
     * 获取文件的临时下载链接。
     */
    DocumentFileAccessVO getFileUrl(Long documentId);

    /**
     * 获取已发布文档文件的临时下载链接。
     */
    DocumentFileAccessVO getPublishedFileUrl(Long documentId);
}
