package com.skillbridge.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"swap_request_id", "reviewer_id"})
}, indexes = {
    @Index(name = "idx_review_swap", columnList = "swap_request_id"),
    @Index(name = "idx_review_reviewee", columnList = "reviewee_id")
})
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_request_id", nullable = false)
    private SwapRequest swapRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Student reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Student reviewee;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    public SwapRequest getSwapRequest() { return swapRequest; }
    public void setSwapRequest(SwapRequest swapRequest) { this.swapRequest = swapRequest; }

    public Student getReviewer() { return reviewer; }
    public void setReviewer(Student reviewer) { this.reviewer = reviewer; }

    public Student getReviewee() { return reviewee; }
    public void setReviewee(Student reviewee) { this.reviewee = reviewee; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
