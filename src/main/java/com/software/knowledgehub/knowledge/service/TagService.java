package com.software.knowledgehub.knowledge.service;

import com.software.knowledgehub.knowledge.dto.CreateTagDTO;
import com.software.knowledgehub.knowledge.dto.UpdateTagDTO;
import com.software.knowledgehub.knowledge.vo.TagVO;

import java.util.List;

public interface TagService {

    /**
     * 创建标签。
     */
    TagVO createTag(CreateTagDTO request);

    /**
     * 查询标签详情。
     */
    TagVO getTag(Long id);

    /**
     * 查询知识库的标签列表。
     */
    List<TagVO> listTags(Long knowledgeBaseId);

    /**
     * 修改标签名称。
     */
    void updateTag(Long id, UpdateTagDTO request);

    /**
     * 删除未被文档使用的标签。
     */
    void deleteTag(Long id);
}
