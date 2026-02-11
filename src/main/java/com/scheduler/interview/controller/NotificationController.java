package com.scheduler.interview.controller;

import com.scheduler.interview.model.Notification;
import com.scheduler.interview.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/interviewer/{interviewerId}")
    public ResponseEntity<Map<String, Object>> getForInterviewer(@PathVariable Long interviewerId) {
        List<Notification> all = notificationService.getForInterviewer(interviewerId);

        List<Map<String, Object>> unread = new ArrayList<>();
        List<Map<String, Object>> read = new ArrayList<>();

        for (Notification n : all) {
            Map<String, Object> dto = toDto(n);
            if (n.isRead()) read.add(dto);
            else unread.add(dto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("interviewerId", interviewerId);
        response.put("unreadCount", unread.size());
        response.put("unreadNotifications", unread);
        response.put("readNotifications", read);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markRead(@PathVariable Long id) {
        Notification n = notificationService.markAsRead(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("notification", toDto(n));
        return ResponseEntity.ok(response);
    }

    private static Map<String, Object> toDto(Notification n) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", n.getId());
        dto.put("type", n.getType());
        dto.put("title", n.getTitle());
        dto.put("message", n.getMessage());
        dto.put("candidateName", n.getCandidateName());
        dto.put("candidateEmail", n.getCandidateEmail());
        dto.put("read", n.isRead());
        dto.put("createdAt", n.getCreatedAt());
        LocalDateTime readAt = n.getReadAt();
        dto.put("readAt", readAt);
        dto.put("bookingId", n.getBooking() != null ? n.getBooking().getId() : null);
        return dto;
    }
}

