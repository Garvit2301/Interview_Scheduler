package com.scheduler.interview.repository;

import com.scheduler.interview.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id")
    Optional<TimeSlot> findByIdWithLock(Long id);
    
    List<TimeSlot> findByStatusAndSlotDateTimeAfterOrderBySlotDateTimeAsc(
        String status, LocalDateTime dateTime);
    
    List<TimeSlot> findByInterviewerIdAndSlotDateTimeBetween(
        Long interviewerId, LocalDateTime start, LocalDateTime end);
}