package com.software.knowledgehub.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "chat-memory")
public class ChatMemoryProperties {

    private int maxMessages;

    private Duration ttl;
}
