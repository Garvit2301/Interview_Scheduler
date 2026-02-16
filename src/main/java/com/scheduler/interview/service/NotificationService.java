package com.scheduler.interview.service;

import com.scheduler.interview.model.Booking;
import com.scheduler.interview.model.Candidate;
import com.scheduler.interview.model.Interviewer;
import com.scheduler.interview.model.Notification;
import com.scheduler.interview.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final int UNREAD_PAGE_SIZE = 10;
    private static final int READ_PAGE_SIZE = 10;

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

    public Page<Notification> getUnreadForInterviewer(Long interviewerId, int page) {
        Pageable pageable = PageRequest.of(page, UNREAD_PAGE_SIZE);
        return notificationRepository.findByInterviewerIdAndReadFalseOrderByCreatedAtDesc(interviewerId, pageable);
    }

    public Page<Notification> getReadForInterviewer(Long interviewerId, int page) {
        Pageable pageable = PageRequest.of(page, READ_PAGE_SIZE);
        return notificationRepository.findByInterviewerIdAndReadTrueOrderByCreatedAtDesc(interviewerId, pageable);
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

