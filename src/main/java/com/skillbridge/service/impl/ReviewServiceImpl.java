package com.skillbridge.service.impl;

import com.skillbridge.dto.ReviewCreationDto;
import com.skillbridge.dto.ReviewDto;
import com.skillbridge.model.entity.Review;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.entity.SwapRequest;
import com.skillbridge.model.enums.NotificationType;
import com.skillbridge.model.enums.RequestStatus;
import com.skillbridge.repository.ReviewRepository;
import com.skillbridge.repository.StudentRepository;
import com.skillbridge.repository.SwapRequestRepository;
import com.skillbridge.service.NotificationService;
import com.skillbridge.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final NotificationService notificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             StudentRepository studentRepository,
                             SwapRequestRepository swapRequestRepository,
                             NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.studentRepository = studentRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void submitReview(String reviewerUsername, ReviewCreationDto dto) {
        Student reviewer = studentRepository.findByUsernameAndIsActiveTrue(reviewerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

        SwapRequest swap = swapRequestRepository.findById(dto.getSwapRequestId())
                .filter(com.skillbridge.model.entity.BaseEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));

        if (swap.getStatus() != RequestStatus.COMPLETED) {
            throw new IllegalStateException("You can only review a COMPLETED swap.");
        }

        Student reviewee;
        if (swap.getRequester().getId().equals(reviewer.getId())) {
            reviewee = swap.getReceiver();
        } else if (swap.getReceiver().getId().equals(reviewer.getId())) {
            reviewee = swap.getRequester();
        } else {
            throw new IllegalArgumentException("You were not part of this swap.");
        }

        if (reviewer.getId().equals(reviewee.getId())) {
            throw new IllegalStateException("You cannot review yourself.");
        }

        if (hasReviewed(reviewerUsername, swap.getId())) {
            throw new IllegalStateException("You have already submitted a review for this swap.");
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setSwapRequest(swap);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        review = reviewRepository.save(review);

        notificationService.createNotification(
                reviewee,
                "You received a new " + dto.getRating() + "★ review.",
                NotificationType.REVIEW_RECEIVED,
                review.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForUser(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return reviewRepository.findByRevieweeIdAndIsActiveTrueOrderByCreatedAtDesc(user.getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return 0.0;
        
        Double avg = reviewRepository.calculateAverageRatingByStudentId(user.getId());
        return avg != null ? avg : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getReviewCount(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return 0;

        Integer count = reviewRepository.countReviewsByStudentId(user.getId());
        return count != null ? count : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewed(String username, Long swapRequestId) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return false;
        
        return reviewRepository.existsByReviewerIdAndSwapRequestIdAndIsActiveTrue(user.getId(), swapRequestId);
    }

    private ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setReviewerUsername(review.getReviewer().getUsername());
        dto.setRevieweeUsername(review.getReviewee().getUsername());
        dto.setSwapRequestId(review.getSwapRequest().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
