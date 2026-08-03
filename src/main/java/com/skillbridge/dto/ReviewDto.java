package com.skillbridge.dto;

import java.time.LocalDateTime;

public class ReviewDto {
    private Long id;
    private String reviewerUsername;
    private String revieweeUsername;
    private Long swapRequestId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReviewerUsername() { return reviewerUsername; }
    public void setReviewerUsername(String reviewerUsername) { this.reviewerUsername = reviewerUsername; }

    public String getRevieweeUsername() { return revieweeUsername; }
    public void setRevieweeUsername(String revieweeUsername) { this.revieweeUsername = revieweeUsername; }

    public Long getSwapRequestId() { return swapRequestId; }
    public void setSwapRequestId(Long swapRequestId) { this.swapRequestId = swapRequestId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
