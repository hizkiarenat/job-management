package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Review;
import com.example.service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable Long reviewId) {
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createReviews(@RequestParam Long companyId, @RequestBody Review review) {
        Boolean createReview = reviewService.addReview(companyId, review);
        if (createReview) {
            return new ResponseEntity<>("Review added successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review not saved!", HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId) {
        return new ResponseEntity<>(reviewService.getAllReviews(companyId), HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
            @RequestBody Review review) {
        boolean isReviewUpdate = reviewService.updateReview(reviewId, review);
        if (isReviewUpdate) {
            return new ResponseEntity<>("Successfully updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review not updated", HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        boolean isReviewDeleted = reviewService.deleteReview(reviewId);
        if (isReviewDeleted) {
            return new ResponseEntity<>("Review deleted Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Review not deleted", HttpStatus.NOT_FOUND);

    }
}
