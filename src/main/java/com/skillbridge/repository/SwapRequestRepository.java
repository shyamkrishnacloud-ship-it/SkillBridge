package com.skillbridge.repository;

import com.skillbridge.model.entity.SwapRequest;
import com.skillbridge.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
    
    @Query("SELECT COUNT(s) FROM SwapRequest s WHERE (s.requester.id = :studentId OR s.receiver.id = :studentId) AND s.status = 'COMPLETED' AND s.isActive = true")
    Integer countCompletedSwapsByStudentId(@Param("studentId") Long studentId);
    
    List<SwapRequest> findByReceiverIdAndStatusAndIsActiveTrue(Long receiverId, RequestStatus status);
    List<SwapRequest> findByRequesterIdAndIsActiveTrue(Long requesterId);
    List<SwapRequest> findByReceiverIdAndIsActiveTrue(Long receiverId);
}
