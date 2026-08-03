package com.skillbridge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewCreationDto {

    @NotNull(message = "Swap Request ID is required")
    private Long swapRequestId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    public Long getSwapRequestId() { return swapRequestId; }
    public void setSwapRequestId(Long swapRequestId) { this.swapRequestId = swapRequestId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
