package com.skillbridge.controller;

import com.skillbridge.model.entity.Notification;
import com.skillbridge.service.NotificationService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificationService notificationService;

    public GlobalControllerAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long addUnreadNotificationCount(Principal principal) {
        if (principal != null) {
            return notificationService.getUnreadCount(principal.getName());
        }
        return 0;
    }

    @ModelAttribute("latestNotifications")
    public List<Notification> addLatestNotifications(Principal principal) {
        if (principal != null) {
            return notificationService.getLatestNotifications(principal.getName());
        }
        return List.of();
    }
}
