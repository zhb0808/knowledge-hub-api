package com.software.knowledgehub.search.listener;

import com.software.knowledgehub.common.config.RabbitMqConfig;
import com.software.knowledgehub.search.message.DocumentIndexRebuildMessage;
import com.software.knowledgehub.search.service.DocumentSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentIndexRebuildListener {

    private final DocumentSearchService documentSearchService;

    /**
     * 消费消息并重建文档搜索索引。
     */
    @RabbitListener(
            queues = RabbitMqConfig.DOCUMENT_INDEX_REBUILD_QUEUE,
            autoStartup = "${document-index.consumer-enabled:true}"
    )
    public void rebuildDocumentIndex(DocumentIndexRebuildMessage message) {
        log.info("开始执行文档索引重建任务，taskId={}", message.getTaskId());
        try {
            // 复用已有全量重建逻辑，方法正常返回后由监听容器自动ACK。
            int documentCount = documentSearchService.rebuildDocumentIndex();
            log.info(
                    "文档索引重建任务完成，taskId={}，documentCount={}",
                    message.getTaskId(),
                    documentCount
            );
        } catch (RuntimeException exception) {
            // 记录任务编号后继续抛出异常，让监听容器拒绝当前消息。
            log.error("文档索引重建任务失败，taskId={}", message.getTaskId(), exception);
            throw exception;
        }
    }
}
