package com.skillbridge.service.impl;

import com.skillbridge.model.entity.Notification;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.enums.NotificationType;
import com.skillbridge.repository.NotificationRepository;
import com.skillbridge.repository.StudentRepository;
import com.skillbridge.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, StudentRepository studentRepository) {
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void createNotification(Student student, String message, NotificationType type, Long referenceId) {
        // Prevent duplicate notification for the exact same event/reference
        if (referenceId != null && notificationRepository.existsByStudentIdAndTypeAndReferenceId(student.getId(), type, referenceId)) {
            return;
        }

        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return 0;
        return notificationRepository.countByStudentIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return List.of();
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getLatestNotifications(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user == null) return List.of();
        return notificationRepository.findTop5ByStudentIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public void markAsRead(Long notificationId, String username) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getStudent().getUsername().equals(username)) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Override
    public void markAllAsRead(String username) {
        Student user = studentRepository.findByUsernameAndIsActiveTrue(username).orElse(null);
        if (user != null) {
            List<Notification> unread = notificationRepository.findByStudentIdAndIsReadFalse(user.getId());
            unread.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(unread);
        }
    }
}
