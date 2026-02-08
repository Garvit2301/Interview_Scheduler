package com.scheduler.interview.service;

import com.scheduler.interview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * AdminService - Handles administrative operations like database reset
 */
@Service
public class AdminService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final BookingRepository bookingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewerRepository interviewerRepository;
    
    public AdminService(BookingRepository bookingRepository,
                       TimeSlotRepository timeSlotRepository,
                       CandidateRepository candidateRepository,
                       InterviewerRepository interviewerRepository) {
        this.bookingRepository = bookingRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.candidateRepository = candidateRepository;
        this.interviewerRepository = interviewerRepository;
    }
    
    /**
     * Reset database - Delete all data from all tables
     * Uses proper order to respect foreign key constraints
     */
    // @Transactional
    // public ResetResult resetDatabase() {

    //     long bookingsDeleted = bookingRepository.count();
    //     bookingRepository.deleteAll();

    //     long timeSlotsDeleted = timeSlotRepository.count();
    //     timeSlotRepository.deleteAll();

    //     long candidatesDeleted = candidateRepository.count();
    //     candidateRepository.deleteAll();

    //     long interviewersDeleted = interviewerRepository.count();
    //     interviewerRepository.deleteAll();

    //     resetAutoIncrement("bookings");
    //     resetAutoIncrement("time_slots");
    //     resetAutoIncrement("candidates");
    //     resetAutoIncrement("interviewers");

    //     entityManager.flush();
    //     entityManager.clear();

    //     return new ResetResult(
    //         true,
    //         "Database reset successful",
    //         bookingsDeleted,
    //         timeSlotsDeleted,
    //         candidatesDeleted,
    //         interviewersDeleted
    //     );
    // }



    @Transactional
    public ResetResult resetDatabase() {

        entityManager.createNativeQuery(
            "TRUNCATE TABLE bookings"
        ).executeUpdate();

        entityManager.createNativeQuery(
            "TRUNCATE TABLE time_slots"
        ).executeUpdate();

        entityManager.createNativeQuery(
            "TRUNCATE TABLE candidates"
        ).executeUpdate();

        entityManager.createNativeQuery(
            "TRUNCATE TABLE interviewers"
        ).executeUpdate();

        return new ResetResult(
            true,
            "Database reset successful",
            0, 0, 0, 0
        );
    }


    
    /**
     * Reset auto-increment counter for a table
     */
    private void resetAutoIncrement(String tableName) {
        try {
            String sql = "ALTER TABLE " + tableName + " AUTO_INCREMENT = 1";
            entityManager.createNativeQuery(sql).executeUpdate();
        } catch (Exception e) {
            // Log error but don't fail the reset operation
            System.err.println("Warning: Could not reset auto-increment for " + tableName);
        }
    }
    
    /**
     * Get current database statistics
     */
    public DatabaseStats getDatabaseStats() {
        return new DatabaseStats(
            bookingRepository.count(),
            timeSlotRepository.count(),
            candidateRepository.count(),
            interviewerRepository.count()
        );
    }
    
    /**
     * Result class for reset operation
     */
    public static class ResetResult {
        private boolean success;
        private String message;
        private long bookingsDeleted;
        private long timeSlotsDeleted;
        private long candidatesDeleted;
        private long interviewersDeleted;
        
        public ResetResult(boolean success, String message,
                          long bookings, long slots, long candidates, long interviewers) {
            this.success = success;
            this.message = message;
            this.bookingsDeleted = bookings;
            this.timeSlotsDeleted = slots;
            this.candidatesDeleted = candidates;
            this.interviewersDeleted = interviewers;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getBookingsDeleted() { return bookingsDeleted; }
        public long getTimeSlotsDeleted() { return timeSlotsDeleted; }
        public long getCandidatesDeleted() { return candidatesDeleted; }
        public long getInterviewersDeleted() { return interviewersDeleted; }
        public long getTotalDeleted() { 
            return bookingsDeleted + timeSlotsDeleted + candidatesDeleted + interviewersDeleted; 
        }
    }
    
    /**
     * Statistics class for database
     */
    public static class DatabaseStats {
        private long totalBookings;
        private long totalTimeSlots;
        private long totalCandidates;
        private long totalInterviewers;
        
        public DatabaseStats(long bookings, long slots, long candidates, long interviewers) {
            this.totalBookings = bookings;
            this.totalTimeSlots = slots;
            this.totalCandidates = candidates;
            this.totalInterviewers = interviewers;
        }
        
        // Getters
        public long getTotalBookings() { return totalBookings; }
        public long getTotalTimeSlots() { return totalTimeSlots; }
        public long getTotalCandidates() { return totalCandidates; }
        public long getTotalInterviewers() { return totalInterviewers; }
        public long getTotalRecords() { 
            return totalBookings + totalTimeSlots + totalCandidates + totalInterviewers; 
        }
    }
}
