package com.skillbridge.service;

import com.skillbridge.model.entity.Notification;
import com.skillbridge.model.entity.Student;
import com.skillbridge.model.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(Student student, String message, NotificationType type, Long referenceId);
    long getUnreadCount(String username);
    List<Notification> getNotificationsForUser(String username);
    List<Notification> getLatestNotifications(String username);
    void markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
}
