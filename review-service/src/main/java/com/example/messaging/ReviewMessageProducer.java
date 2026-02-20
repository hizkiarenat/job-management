package com.example.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.dto.ReviewMessage;
import com.example.model.Review;

@Service
public class ReviewMessageProducer {

    private RabbitTemplate rabbitTemplate;

    public ReviewMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Review review) {
        ReviewMessage reviewMessage = new ReviewMessage();
        reviewMessage.setId(review.getId());
        reviewMessage.setTitle(review.getTitle());
        reviewMessage.setDescription(review.getDescription());
        reviewMessage.setRating(review.getRating());
        reviewMessage.setCompanyId(review.getCompanyId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.EXCHANGE,
                RabbitMQConfiguration.ROUTING_KEY,
                reviewMessage
            );
    }
}
