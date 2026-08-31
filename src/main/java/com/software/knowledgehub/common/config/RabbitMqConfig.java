package com.software.knowledgehub.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DOCUMENT_INDEX_EXCHANGE = "knowledge-hub.search.exchange";
    public static final String DOCUMENT_INDEX_REBUILD_QUEUE =
            "knowledge-hub.document-index-rebuild.queue";
    public static final String DOCUMENT_INDEX_REBUILD_ROUTING_KEY = "document.index.rebuild";

    @Bean
    public DirectExchange documentIndexExchange() {
        return new DirectExchange(DOCUMENT_INDEX_EXCHANGE, true, false);
    }

    @Bean
    public Queue documentIndexRebuildQueue() {
        return QueueBuilder.durable(DOCUMENT_INDEX_REBUILD_QUEUE).build();
    }

    @Bean
    public Binding documentIndexRebuildBinding(
            DirectExchange documentIndexExchange,
            Queue documentIndexRebuildQueue) {
        return BindingBuilder.bind(documentIndexRebuildQueue)
                .to(documentIndexExchange)
                .with(DOCUMENT_INDEX_REBUILD_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(
                objectMapper,
                "com.software.knowledgehub.search.message"
        );
    }
}
