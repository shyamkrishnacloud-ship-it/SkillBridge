package com.skillbridge.repository;

import com.skillbridge.model.entity.Notification;
import com.skillbridge.model.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Notification> findByStudentIdAndIsReadFalse(Long studentId);
    long countByStudentIdAndIsReadFalse(Long studentId);
    List<Notification> findTop5ByStudentIdOrderByCreatedAtDesc(Long studentId);
    boolean existsByStudentIdAndTypeAndReferenceId(Long studentId, NotificationType type, Long referenceId);
}
