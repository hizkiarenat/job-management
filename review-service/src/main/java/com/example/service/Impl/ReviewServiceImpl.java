package com.example.service.Impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.messaging.ReviewMessageProducer;
import com.example.model.Review;
import com.example.repository.ReviewRepository;
import com.example.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewRepository reviewRepository;
    private final ReviewMessageProducer messageProducer;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMessageProducer messageProducer) {
        this.reviewRepository = reviewRepository;
        this.messageProducer = messageProducer;
    }

    @Override
    public boolean addReview(Long companyId, Review review) {
        if (companyId != null && review != null) {
            review.setCompanyId(companyId);

            Review savedReview = reviewRepository.save(review);

            // send message rabbitMQ
            try {
                messageProducer.sendMessage(savedReview);
                logger.info("Review created and event published for company: {}", companyId);
            } catch (Exception e) {
                logger.error("Failed to publish review event: {}", e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            reviewRepository.delete(review);
            return true;
        }
        return false;
    }

    @Override
    public List<Review> getAllReviews(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        return reviews;
    }

    @Override
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    @Override
    public boolean updateReview(Long reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            review.setTitle(updatedReview.getTitle());
            review.setDescription(updatedReview.getDescription());
            review.setRating(updatedReview.getRating());
            review.setCompanyId(updatedReview.getCompanyId());
            Review savedReview = reviewRepository.save(review);

            // send message to rabbitMQ
            try {
                messageProducer.sendMessage(savedReview);
                logger.info("Review updated and event published: {}", reviewId);
            } catch (Exception e) {
                logger.error("Failed to publish review update event: {}", e.getMessage(), e);
            }
            return true;
        }
        return false;
    }

}
