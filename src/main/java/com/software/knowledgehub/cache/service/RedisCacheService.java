package com.software.knowledgehub.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.knowledgehub.cache.config.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 读写 Redis 中的 JSON 缓存数据。
 */
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    /**
     * 按缓存键读取并转换缓存数据。
     */
    public <T> T getCacheValue(String key, Class<T> valueType) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value, valueType);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("缓存数据无法解析", exception);
        }
    }

    /**
     * 将对象转换为 JSON 后写入缓存。
     */
    public void setCacheValue(String key, Object value) {
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    cacheProperties.getDetailTtl()
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("缓存数据无法序列化", exception);
        }
    }

    /**
     * 删除指定缓存键。
     */
    public void deleteCacheValue(String key) {
        stringRedisTemplate.delete(key);
    }
}
