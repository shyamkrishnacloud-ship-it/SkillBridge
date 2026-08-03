package com.skillbridge.controller;

import com.skillbridge.model.entity.Notification;
import com.skillbridge.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String viewNotifications(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        List<Notification> notifications = notificationService.getNotificationsForUser(principal.getName());
        model.addAttribute("notifications", notifications);
        return "notifications/list";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        notificationService.markAsRead(id, principal.getName());
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(Principal principal) {
        if (principal == null) return "redirect:/login";
        notificationService.markAllAsRead(principal.getName());
        return "redirect:/notifications";
    }
}
