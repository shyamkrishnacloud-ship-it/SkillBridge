package com.skillbridge.repository;

import com.skillbridge.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :studentId AND r.isActive = true")
    Double calculateAverageRatingByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee.id = :studentId AND r.isActive = true")
    Integer countReviewsByStudentId(@Param("studentId") Long studentId);
    
    List<Review> findByRevieweeIdAndIsActiveTrueOrderByCreatedAtDesc(Long revieweeId);

    boolean existsByReviewerIdAndSwapRequestIdAndIsActiveTrue(Long reviewerId, Long swapRequestId);
}
