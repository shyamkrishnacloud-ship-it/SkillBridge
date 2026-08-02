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

    List<SwapRequest> findByRequester_UsernameAndIsActiveTrueOrderByCreatedAtDesc(String username);
    List<SwapRequest> findByReceiver_UsernameAndIsActiveTrueOrderByCreatedAtDesc(String username);
    
    boolean existsByRequesterAndReceiverAndOfferedSkillAndRequestedSkillAndStatusAndIsActiveTrue(
            com.skillbridge.model.entity.Student requester,
            com.skillbridge.model.entity.Student receiver,
            com.skillbridge.model.entity.StudentSkill offeredSkill,
            com.skillbridge.model.entity.StudentSkill requestedSkill,
            RequestStatus status
    );

    @Query("SELECT COUNT(s) > 0 FROM SwapRequest s WHERE " +
           "((s.requester.id = :user1Id AND s.receiver.id = :user2Id) OR " +
           "(s.requester.id = :user2Id AND s.receiver.id = :user1Id)) " +
           "AND s.status IN :statuses AND s.isActive = true")
    boolean existsActiveRequestBetweenUsers(@Param("user1Id") Long user1Id,
                                            @Param("user2Id") Long user2Id,
                                            @Param("statuses") List<RequestStatus> statuses);
}
