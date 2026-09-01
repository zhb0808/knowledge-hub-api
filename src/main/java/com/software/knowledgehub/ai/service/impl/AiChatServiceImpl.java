package com.software.knowledgehub.ai.service.impl;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.service.AiChatService;
import com.software.knowledgehub.ai.vo.AiChatVO;
import com.software.knowledgehub.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private static final String SYSTEM_PROMPT = """
            你是企业知识库平台中的通用 AI 助手。
            请使用准确、清晰、简洁的中文回答用户问题。
            如果无法确定答案，请明确说明不知道，不要编造事实。
            """;

    private final ChatClient chatClient;

    /**
     * 同步生成 AI 回答。
     */
    @Override
    public AiChatVO chat(AiChatDTO request) {
        try {
            // 将固定系统提示词和本次用户问题提交给大模型。
            String content = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(request.getMessage())
                    .call()
                    .content();
            return new AiChatVO(content);
        } catch (Exception exception) {
            log.error("调用大模型生成同步回答失败", exception);
            throw new BusinessException("AI回答生成失败，请稍后重试");
        }
    }

    /**
     * 流式生成 AI 回答片段。
     */
    @Override
    public Flux<String> streamChat(AiChatDTO request) {
        // 订阅后向大模型发起流式请求，并将到达的回答片段继续发送给客户端。
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(request.getMessage())
                .stream()
                .content()
                .doOnError(exception -> log.error("调用大模型生成流式回答失败", exception));
    }
}
