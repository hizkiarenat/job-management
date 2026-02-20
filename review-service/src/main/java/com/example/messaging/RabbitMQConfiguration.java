package com.example.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    // -------- CONSTANT -----------
    public static final String QUEUE = "companyRatingQueue";
    public static final String EXCHANGE = "review.exchange";
    public static final String ROUTING_KEY = "review.rating.update";

    // ---------- QUEUE -------------
    @Bean
    public Queue companyRatingQueue() {
        return new Queue(QUEUE, true);
    }

    // ---------- EXCHANGE -----------
    @Bean
    public TopicExchange reviewExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // ----------- BINDING -------------
    @Bean
    public Binding binding(Queue companyRatingQueue, TopicExchange reviewExchange) {
        return BindingBuilder
                .bind(companyRatingQueue)
                .to(reviewExchange)
                .with(ROUTING_KEY);
    }

    // --------- MESSAGE CONVERTER ---------------
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ---------- RABBIT TEMPLATE ---------------
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
