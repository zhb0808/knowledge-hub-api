package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.cache.service.RedisCacheService;
import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.dto.BatchUpdateDocumentStatusDTO;
import com.software.knowledgehub.knowledge.dto.CreateDocumentDTO;
import com.software.knowledgehub.knowledge.dto.DocumentQueryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateDocumentDTO;
import com.software.knowledgehub.knowledge.entity.KbCategory;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.entity.KbTag;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.repository.KbTagRepository;
import com.software.knowledgehub.knowledge.service.DocumentService;
import com.software.knowledgehub.knowledge.service.DocumentFileService;
import com.software.knowledgehub.knowledge.vo.DocumentListVO;
import com.software.knowledgehub.knowledge.vo.DocumentVO;
import com.software.knowledgehub.knowledge.vo.TagVO;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

    private static final String DOCUMENT_CACHE_KEY_PREFIX = "cache:document:";

    private final KbDocumentRepository documentRepository;
    private final KbKnowledgeBaseRepository knowledgeBaseRepository;
    private final KbCategoryRepository categoryRepository;
    private final KbTagRepository tagRepository;
    private final DocumentFileService documentFileService;
    private final RedisCacheService redisCacheService;

    /**
     * 创建文档及其标签关系。
     */
    @Override
    @Transactional
    public DocumentVO createDocument(CreateDocumentDTO request) {
        // 加载文档所属知识库。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository
                .findById(request.getKnowledgeBaseId())
                .orElseThrow(() -> new BusinessException("知识库不存在"));

        // 加载并校验分类、标签的知识库归属。
        KbCategory category = loadCategory(
                request.getCategoryId(),
                knowledgeBase.getId()
        );
        List<KbTag> tags = loadTags(request.getTagIds(), knowledgeBase.getId());

        KbDocument document = new KbDocument();
        document.setKnowledgeBase(knowledgeBase);
        document.setCategory(category);
        document.setTitle(request.getTitle().strip());
        document.setSummary(request.getSummary());
        document.setContent(request.getContent());
        document.setStatus("DRAFT");
        document.getTags().addAll(tags);

        // 保存文档，Hibernate 同步维护文档标签关系表。
        return toDocumentVO(documentRepository.save(document));
    }

    /**
     * 查询文档详情。
     */
    @Override
    public DocumentVO getDocument(Long id) {
        String cacheKey = DOCUMENT_CACHE_KEY_PREFIX + id;
        DocumentVO cachedDocument = redisCacheService.getCacheValue(cacheKey, DocumentVO.class);
        if (cachedDocument != null) {
            return cachedDocument;
        }

        // 在只读事务中加载文档并组装关联展示字段。
        KbDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));
        DocumentVO documentVO = toDocumentVO(document);

        // 数据库命中后回填详情缓存。
        redisCacheService.setCacheValue(cacheKey, documentVO);
        return documentVO;
    }

    /**
     * 分页查询知识库中的文档。
     */
    @Override
    public Page<DocumentListVO> listDocuments(
            DocumentQueryDTO request,
            Pageable pageable) {
        // 先确认知识库存在，区分空分页与无效知识库。
        if (!knowledgeBaseRepository.existsById(request.getKnowledgeBaseId())) {
            throw new BusinessException("知识库不存在");
        }

        // 根据非空筛选条件构造动态查询。
        Specification<KbDocument> specification = (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(
                    root.get("knowledgeBase").get("id"),
                    request.getKnowledgeBaseId()
            ));
            if (request.getCategoryId() != null) {
                predicates.add(builder.equal(root.get("category").get("id"), request.getCategoryId()));
            }
            if (request.getTagId() != null) {
                Join<KbDocument, KbTag> tag = root.join("tags", JoinType.INNER);
                predicates.add(builder.equal(tag.get("id"), request.getTagId()));
                criteriaQuery.distinct(true);
            }
            if (request.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), request.getStatus()));
            }
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String keyword = "%" + request.getKeyword().strip() + "%";
                predicates.add(builder.or(
                        builder.like(root.get("title"), keyword),
                        builder.like(root.get("summary"), keyword)
                ));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        // 先分页查询文档主表，避免集合关联影响分页结果。
        Page<KbDocument> documentPage = documentRepository.findAll(specification, pageable);
        List<Long> documentIds = documentPage.getContent().stream()
                .map(KbDocument::getId)
                .toList();

        // 一次加载当前页的分类和标签，避免逐条访问关联数据。
        Map<Long, KbDocument> documentMap = documentRepository.findByIdIn(documentIds)
                .stream()
                .collect(Collectors.toMap(KbDocument::getId, document -> document));
        List<DocumentListVO> documents = documentIds.stream()
                .map(documentMap::get)
                .map(this::toDocumentListVO)
                .toList();
        return new PageImpl<>(documents, pageable, documentPage.getTotalElements());
    }

    /**
     * 批量修改知识库内的文档状态。
     */
    @Override
    @Transactional
    public int batchUpdateStatus(BatchUpdateDocumentStatusDTO request) {
        // 确认批量操作所属的知识库存在。
        if (!knowledgeBaseRepository.existsById(request.getKnowledgeBaseId())) {
            throw new BusinessException("知识库不存在");
        }

        // JPQL 批量更新绕过 JPA Auditing，需要显式写入修改人和修改时间。
        AuthenticatedUser currentUser = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        int updatedCount = documentRepository.updateStatusByKnowledgeBaseIdAndIdIn(
                request.getKnowledgeBaseId(),
                request.getDocumentIds(),
                request.getStatus(),
                currentUser.getId(),
                OffsetDateTime.now()
        );

        // 删除请求范围内的详情缓存，避免继续读取旧状态。
        request.getDocumentIds().forEach(documentId ->
                redisCacheService.deleteCacheValue(DOCUMENT_CACHE_KEY_PREFIX + documentId));
        return updatedCount;
    }

    /**
     * 修改文档内容及其关联关系。
     */
    @Override
    @Transactional
    public void updateDocument(Long id, UpdateDocumentDTO request) {
        // 加载需要修改的文档。
        KbDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));
        Long knowledgeBaseId = document.getKnowledgeBase().getId();
        if (!document.getVersion().equals(request.getVersion())) {
            throw new BusinessException("文档已被其他用户修改，请刷新后重试");
        }

        // 加载并校验新的分类、标签关系。
        KbCategory category = loadCategory(request.getCategoryId(), knowledgeBaseId);
        List<KbTag> tags = loadTags(request.getTagIds(), knowledgeBaseId);

        document.setCategory(category);
        document.setTitle(request.getTitle().strip());
        document.setSummary(request.getSummary());
        document.setContent(request.getContent());
        document.setStatus(request.getStatus());

        // 修改托管集合，Hibernate 根据差异更新关系表。
        document.getTags().clear();
        document.getTags().addAll(tags);

        try {
            // 立即执行更新，捕获同一时刻提交的版本冲突。
            documentRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new BusinessException("文档已被其他用户修改，请刷新后重试");
        }

        // 修改后删除旧缓存，下一次详情查询重新加载关联数据。
        redisCacheService.deleteCacheValue(DOCUMENT_CACHE_KEY_PREFIX + id);
    }

    /**
     * 删除文档、标签关系及关联文件。
     */
    @Override
    @Transactional
    public void deleteDocument(Long id) {
        // 加载文档及其标签集合。
        KbDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文档不存在"));

        // 先删除对象存储中的文件和文件元数据。
        documentFileService.deleteFile(id);

        // 先解除拥有方维护的标签关系，再删除文档。
        document.getTags().clear();
        documentRepository.delete(document);

        // 删除文档后不再保留可能失效的详情缓存。
        redisCacheService.deleteCacheValue(DOCUMENT_CACHE_KEY_PREFIX + id);
    }

    private KbCategory loadCategory(Long categoryId, Long knowledgeBaseId) {
        if (categoryId == null) {
            return null;
        }

        // 加载分类并校验知识库归属。
        KbCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        if (!category.getKnowledgeBase().getId().equals(knowledgeBaseId)) {
            throw new BusinessException("分类必须属于文档所在知识库");
        }
        return category;
    }

    private List<KbTag> loadTags(Set<Long> tagIds, Long knowledgeBaseId) {
        if (tagIds.isEmpty()) {
            return List.of();
        }

        // 一次查询加载同一知识库中的全部目标标签。
        List<KbTag> tags = tagRepository
                .findAllByKnowledgeBaseIdAndIdIn(knowledgeBaseId, tagIds);
        if (tags.size() != tagIds.size()) {
            throw new BusinessException("存在无效标签或标签不属于文档所在知识库");
        }
        return tags;
    }

    private DocumentListVO toDocumentListVO(KbDocument document) {
        Long categoryId = document.getCategory() == null
                ? null
                : document.getCategory().getId();
        String categoryName = document.getCategory() == null
                ? null
                : document.getCategory().getName();
        List<TagVO> tags = document.getTags().stream()
                .sorted(Comparator.comparing(KbTag::getName))
                .map(tag -> new TagVO(
                        tag.getId(),
                        tag.getKnowledgeBase().getId(),
                        tag.getName(),
                        tag.getCreatedTime(),
                        tag.getUpdatedTime()
                ))
                .toList();
        return new DocumentListVO(
                document.getId(),
                document.getKnowledgeBase().getId(),
                categoryId,
                categoryName,
                document.getTitle(),
                document.getSummary(),
                document.getStatus(),
                tags,
                document.getCreatedTime(),
                document.getUpdatedTime()
        );
    }

    private DocumentVO toDocumentVO(KbDocument document) {
        Long categoryId = document.getCategory() == null
                ? null
                : document.getCategory().getId();
        String categoryName = document.getCategory() == null
                ? null
                : document.getCategory().getName();
        List<TagVO> tags = document.getTags().stream()
                .sorted(Comparator.comparing(KbTag::getName))
                .map(tag -> new TagVO(
                        tag.getId(),
                        tag.getKnowledgeBase().getId(),
                        tag.getName(),
                        tag.getCreatedTime(),
                        tag.getUpdatedTime()
                ))
                .toList();
        return new DocumentVO(
                document.getId(),
                document.getKnowledgeBase().getId(),
                categoryId,
                categoryName,
                document.getTitle(),
                document.getSummary(),
                document.getContent(),
                document.getStatus(),
                document.getVersion(),
                tags,
                document.getCreatedTime(),
                document.getUpdatedTime()
        );
    }

}
