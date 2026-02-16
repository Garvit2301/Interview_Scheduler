package com.scheduler.interview.repository;

import com.scheduler.interview.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByInterviewerIdAndReadFalseOrderByCreatedAtDesc(Long interviewerId, Pageable pageable);
    Page<Notification> findByInterviewerIdAndReadTrueOrderByCreatedAtDesc(Long interviewerId, Pageable pageable);
    long countByInterviewerIdAndReadFalse(Long interviewerId);
}

