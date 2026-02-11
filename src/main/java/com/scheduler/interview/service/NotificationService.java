package com.scheduler.interview.service;

import com.scheduler.interview.model.Booking;
import com.scheduler.interview.model.Candidate;
import com.scheduler.interview.model.Interviewer;
import com.scheduler.interview.model.Notification;
import com.scheduler.interview.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification create(
            Interviewer interviewer,
            Booking booking,
            String type,
            String title,
            String message,
            Candidate candidate
    ) {
        Notification n = new Notification();
        n.setInterviewer(interviewer);
        n.setBooking(booking);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        if (candidate != null) {
            n.setCandidateName(candidate.getName());
            n.setCandidateEmail(candidate.getEmail());
        }
        return notificationRepository.save(n);
    }

    public List<Notification> getForInterviewer(Long interviewerId) {
        return notificationRepository.findByInterviewerIdOrderByCreatedAtDesc(interviewerId);
    }

    public long countUnreadForInterviewer(Long interviewerId) {
        return notificationRepository.countByInterviewerIdAndReadFalse(interviewerId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.isRead()) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        }
        return n;
    }
}

