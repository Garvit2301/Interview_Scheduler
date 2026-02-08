package com.scheduler.interview.repository;

import com.scheduler.interview.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.candidate.id = :candidateId " +
           "AND b.status = 'CONFIRMED' AND b.timeSlot.slotDateTime > :now")
    List<Booking> findActiveFutureBookings(Long candidateId, LocalDateTime now);
    
    List<Booking> findByInterviewerIdAndStatusOrderByBookedAtDesc(
        Long interviewerId, String status);

    @Query("SELECT b FROM Booking b WHERE b.candidate.id = :candidateId")
    List<Booking> findAllByCandidateIdRaw(Long candidateId);
}
