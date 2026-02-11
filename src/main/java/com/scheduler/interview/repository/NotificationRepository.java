package com.scheduler.interview.repository;

import com.scheduler.interview.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByInterviewerIdOrderByCreatedAtDesc(Long interviewerId);
    long countByInterviewerIdAndReadFalse(Long interviewerId);
}

