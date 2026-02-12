package com.example.job_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.job_service.external.Review;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/reviews")
    List<Review> getReview(@RequestParam("companyId") Long companyId);
}
