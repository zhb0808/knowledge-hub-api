package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.entity.KbFile;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbFileRepository;
import com.software.knowledgehub.knowledge.service.DocumentFileService;
import com.software.knowledgehub.knowledge.vo.DocumentFileAccessVO;
import com.software.knowledgehub.knowledge.vo.DocumentFileVO;
import com.software.knowledgehub.storage.config.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentFileServiceImpl implements DocumentFileService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "txt", "png", "jpg", "jpeg"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "image/png",
            "image/jpeg"
    );

    private final KbDocumentRepository documentRepository;
    private final KbFileRepository fileRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 为文档首次上传文件。
     */
    @Override
    @Transactional
    public DocumentFileVO uploadFile(Long documentId, MultipartFile file) {
        validateFile(file);

        // 加载目标文档并确认尚未关联文件。
        KbDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在"));
        if (fileRepository.findByDocumentId(documentId).isPresent()) {
            throw new BusinessException("文档已关联文件，请使用替换接口");
        }

        String objectName = buildObjectName(documentId, file);
        boolean uploaded = false;
        try {
            // 先将实际文件写入对象存储。
            uploadObject(objectName, file);
            uploaded = true;

            // 保存文件元数据，并立即执行 SQL 以便上传失败时清理对象。
            KbFile documentFile = new KbFile();
            documentFile.setDocument(document);
            documentFile.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
            documentFile.setObjectName(objectName);
            documentFile.setContentType(file.getContentType());
            documentFile.setFileSize(file.getSize());
            return toDocumentFileVO(fileRepository.saveAndFlush(documentFile));
        } catch (Exception exception) {
            if (uploaded) {
                try {
                    removeObject(objectName);
                } catch (Exception cleanupException) {
                    log.error("清理上传失败的 MinIO 对象失败: {}", objectName, cleanupException);
                }
            }
            log.error("文档文件上传失败", exception);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 替换文档已关联的文件。
     */
    @Override
    @Transactional
    public DocumentFileVO replaceFile(Long documentId, MultipartFile file) {
        validateFile(file);

        // 加载已有文件，替换后仍沿用同一条元数据记录。
        KbFile documentFile = fileRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new BusinessException("当前文档尚未上传文件，请使用上传接口"));

        String oldObjectName = documentFile.getObjectName();
        String objectName = buildObjectName(documentId, file);
        boolean uploaded = false;
        try {
            // 先写入新对象，避免删除旧文件后上传失败。
            uploadObject(objectName, file);
            uploaded = true;

            documentFile.setOriginalName(StringUtils.cleanPath(file.getOriginalFilename()));
            documentFile.setObjectName(objectName);
            documentFile.setContentType(file.getContentType());
            documentFile.setFileSize(file.getSize());
            KbFile savedFile = fileRepository.saveAndFlush(documentFile);

            // 元数据已切换到新对象后，再删除旧对象。
            removeObject(oldObjectName);
            return toDocumentFileVO(savedFile);
        } catch (Exception exception) {
            if (uploaded) {
                try {
                    removeObject(objectName);
                } catch (Exception cleanupException) {
                    log.error("清理替换失败的新对象失败: {}", objectName, cleanupException);
                }
            }
            log.error("文档文件替换失败", exception);
            throw new BusinessException("文件替换失败");
        }
    }

    /**
     * 删除文档关联的文件。
     */
    @Override
    @Transactional
    public void deleteFile(Long documentId) {
        // 确认文档存在，避免删除无效文档的文件请求被静默忽略。
        documentRepository.findById(documentId)
                .orElseThrow(() -> new BusinessException("文档不存在"));
        KbFile documentFile = fileRepository.findByDocumentId(documentId).orElse(null);
        if (documentFile == null) {
            return;
        }

        try {
            // 先删除元数据并刷新，MinIO 删除失败时事务会回滚。
            fileRepository.delete(documentFile);
            fileRepository.flush();
            removeObject(documentFile.getObjectName());
        } catch (Exception exception) {
            log.error("文档文件删除失败", exception);
            throw new BusinessException("文件删除失败");
        }
    }

    /**
     * 获取文件的临时下载链接。
     */
    @Override
    public DocumentFileAccessVO getFileUrl(Long documentId) {
        // 根据文档加载文件元数据。
        KbFile documentFile = fileRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new BusinessException("文档尚未上传文件"));

        try {
            // 生成私有对象 10 分钟内有效的下载地址。
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucket())
                            .object(documentFile.getObjectName())
                            .expiry(10, TimeUnit.MINUTES)
                            .build()
            );
            return new DocumentFileAccessVO(
                    documentFile.getOriginalName(),
                    url,
                    OffsetDateTime.now().plusMinutes(10)
            );
        } catch (Exception exception) {
            log.error("生成文件预签名链接失败", exception);
            throw new BusinessException("获取文件下载链接失败");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过20MB");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("仅支持 PDF、Word、TXT、PNG 和 JPG 文件");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("文件类型不符合要求");
        }
    }

    private String buildObjectName(Long documentId, MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(
                StringUtils.cleanPath(file.getOriginalFilename())
        );
        return "documents/" + documentId + "/" + UUID.randomUUID()
                + "." + extension.toLowerCase();
    }

    private void uploadObject(String objectName, MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    private void removeObject(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectName)
                        .build()
        );
    }

    private DocumentFileVO toDocumentFileVO(KbFile documentFile) {
        return new DocumentFileVO(
                documentFile.getId(),
                documentFile.getDocument().getId(),
                documentFile.getOriginalName(),
                documentFile.getContentType(),
                documentFile.getFileSize(),
                documentFile.getCreatedTime(),
                documentFile.getUpdatedTime()
        );
    }
}
