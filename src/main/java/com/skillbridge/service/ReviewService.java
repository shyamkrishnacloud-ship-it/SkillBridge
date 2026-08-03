package com.skillbridge.service;

import com.skillbridge.dto.ReviewCreationDto;
import com.skillbridge.dto.ReviewDto;
import java.util.List;

public interface ReviewService {
    void submitReview(String reviewerUsername, ReviewCreationDto reviewCreationDto);
    List<ReviewDto> getReviewsForUser(String username);
    Double getAverageRating(String username);
    Integer getReviewCount(String username);
    boolean hasReviewed(String username, Long swapRequestId);
}
