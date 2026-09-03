package com.software.knowledgehub.ai.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.knowledgehub.ai.config.ChatMemoryProperties;
import com.software.knowledgehub.ai.model.AiChatMemoryMessage;
import com.software.knowledgehub.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private static final String KEY_PREFIX = "ai:chat:memory:";
    private static final String CONVERSATION_INDEX_KEY = "ai:chat:memory:conversations";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMemoryProperties chatMemoryProperties;

    @Override
    public List<String> findConversationIds() {
        // 读取已经登记的对话编号。
        Set<String> conversationIds = stringRedisTemplate.opsForSet().members(CONVERSATION_INDEX_KEY);
        if (conversationIds == null || conversationIds.isEmpty()) {
            return List.of();
        }

        List<String> activeConversationIds = new ArrayList<>();
        for (String conversationId : conversationIds) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildKey(conversationId)))) {
                activeConversationIds.add(conversationId);
                continue;
            }
            // 消息过期后，同时清理对话编号。
            stringRedisTemplate.opsForSet().remove(CONVERSATION_INDEX_KEY, conversationId);
        }
        return activeConversationIds;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        // 从 Redis 读取当前对话的 JSON。
        String messageJson = stringRedisTemplate.opsForValue().get(buildKey(conversationId));
        if (messageJson == null) {
            return List.of();
        }

        try {
            List<AiChatMemoryMessage> storedMessages = objectMapper.readValue(
                    messageJson,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class,
                            AiChatMemoryMessage.class
                    )
            );
            List<Message> messages = new ArrayList<>();
            // 根据消息角色恢复 Spring AI 消息对象。
            for (AiChatMemoryMessage storedMessage : storedMessages) {
                MessageType messageType = MessageType.valueOf(storedMessage.getType());
                if (messageType == MessageType.USER) {
                    messages.add(new UserMessage(storedMessage.getContent()));
                    continue;
                }
                if (messageType == MessageType.ASSISTANT) {
                    messages.add(new AssistantMessage(storedMessage.getContent()));
                    continue;
                }
                if (messageType == MessageType.SYSTEM) {
                    messages.add(new SystemMessage(storedMessage.getContent()));
                }
            }
            return messages;
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            log.error("读取Redis对话记忆失败，conversationId={}", conversationId, exception);
            throw new BusinessException("读取对话记忆失败，请重新开始对话");
        }
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (messages.isEmpty()) {
            deleteByConversationId(conversationId);
            return;
        }

        // 将 Spring AI 消息转换成适合写入 Redis 的简单对象。
        List<AiChatMemoryMessage> storedMessages = messages.stream()
                .filter(message -> message.getMessageType() != MessageType.TOOL)
                .map(message -> new AiChatMemoryMessage(
                        message.getMessageType().name(),
                        message.getText()
                ))
                .toList();
        try {
            String messageJson = objectMapper.writeValueAsString(storedMessages);
            // 保存消息，并刷新当前对话的过期时间。
            stringRedisTemplate.opsForValue().set(
                    buildKey(conversationId),
                    messageJson,
                    chatMemoryProperties.getTtl()
            );
            // 记录已经出现过的对话编号。
            stringRedisTemplate.opsForSet().add(CONVERSATION_INDEX_KEY, conversationId);
        } catch (JsonProcessingException exception) {
            log.error("保存Redis对话记忆失败，conversationId={}", conversationId, exception);
            throw new BusinessException("保存对话记忆失败，请稍后重试");
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        // 同时删除消息和对话编号。
        stringRedisTemplate.delete(buildKey(conversationId));
        stringRedisTemplate.opsForSet().remove(CONVERSATION_INDEX_KEY, conversationId);
    }

    private String buildKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }
}
