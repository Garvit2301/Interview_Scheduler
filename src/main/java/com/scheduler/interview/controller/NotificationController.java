package com.scheduler.interview.controller;

import com.scheduler.interview.model.Notification;
import com.scheduler.interview.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/interviewer/{interviewerId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long interviewerId) {
        long count = notificationService.countUnreadForInterviewer(interviewerId);
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interviewer/{interviewerId}/unread")
    public ResponseEntity<Map<String, Object>> getUnread(
            @PathVariable Long interviewerId,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<Notification> unreadPage = notificationService.getUnreadForInterviewer(interviewerId, page);
        List<Map<String, Object>> dtos = unreadPage.getContent().stream()
                .map(NotificationController::toDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("totalUnreadCount", notificationService.countUnreadForInterviewer(interviewerId));
        response.put("totalElements", unreadPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interviewer/{interviewerId}/read")
    public ResponseEntity<Map<String, Object>> getRead(
            @PathVariable Long interviewerId,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<Notification> readPage = notificationService.getReadForInterviewer(interviewerId, page);
        List<Map<String, Object>> dtos = readPage.getContent().stream()
                .map(NotificationController::toDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("totalPages", readPage.getTotalPages());
        response.put("totalElements", readPage.getTotalElements());
        response.put("number", readPage.getNumber());
        response.put("first", readPage.isFirst());
        response.put("last", readPage.isLast());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
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

