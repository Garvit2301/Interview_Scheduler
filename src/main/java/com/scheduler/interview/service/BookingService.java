package com.scheduler.interview.service;

import com.scheduler.interview.model.*;
import com.scheduler.interview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final TimeSlotRepository slotRepository;
    private final CandidateRepository candidateRepository;
    
    public BookingService(BookingRepository bookingRepository,
                         TimeSlotRepository slotRepository,
                         CandidateRepository candidateRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.candidateRepository = candidateRepository;
    }
    
    /**
     * Book a slot with race condition handling via optimistic locking
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking bookSlot(Long candidateId, Long slotId, String notes) {
        
        // 1. Check if candidate already has active booking
        List<Booking> existing = bookingRepository.findActiveFutureBookings(
            candidateId, LocalDateTime.now());
        
        if (!existing.isEmpty()) {
            throw new RuntimeException("Candidate already has an active booking");
        }
        
        // 2. Get candidate
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new RuntimeException("Candidate not found"));
        
        // 3. Get slot with optimistic lock
        TimeSlot slot = slotRepository.findByIdWithLock(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));
        
        // 4. Check if slot is available
        if (!"AVAILABLE".equals(slot.getStatus())) {
            throw new RuntimeException("Slot is no longer available");
        }
        
        // 5. Check if slot is in the future
        if (slot.getSlotDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book past slots");
        }
        
        try {
            // 6. Mark slot as booked (optimistic lock will prevent race condition)
            slot.setStatus("BOOKED");
            slotRepository.save(slot);
            
            // 7. Create booking
            Booking booking = new Booking();
            booking.setCandidate(candidate);
            booking.setTimeSlot(slot);
            booking.setInterviewer(slot.getInterviewer());
            booking.setStatus("CONFIRMED");
            booking.setNotes(notes);
            return bookingRepository.save(booking);
            
        } catch (OptimisticLockException e) {
            throw new RuntimeException(
                "This slot was just booked by another candidate. Please try another slot.");
        }
    }
    
    // /**
    //  * Update/Reschedule a booking
    //  */
    // @Transactional(isolation = Isolation.READ_COMMITTED)
    // public Booking updateBooking(Long bookingId, Long newSlotId) {
        
    //     Booking booking = bookingRepository.findById(bookingId)
    //         .orElseThrow(() -> new RuntimeException("Booking not found"));
        
    //     if (!"CONFIRMED".equals(booking.getStatus())) {
    //         throw new RuntimeException("Can only reschedule confirmed bookings");
    //     }
        
    //     // Get old and new slots with lock
    //     TimeSlot oldSlot = slotRepository.findByIdWithLock(booking.getTimeSlot().getId())
    //         .orElseThrow(() -> new RuntimeException("Old slot not found"));
        
    //     TimeSlot newSlot = slotRepository.findByIdWithLock(newSlotId)
    //         .orElseThrow(() -> new RuntimeException("New slot not found"));
        
    //     if (!"AVAILABLE".equals(newSlot.getStatus())) {
    //         throw new RuntimeException("New slot is not available");
    //     }
        
    //     try {
    //         // Release old slot
    //         oldSlot.setStatus("AVAILABLE");
    //         slotRepository.save(oldSlot);
            
    //         // Book new slot
    //         newSlot.setStatus("BOOKED");
    //         slotRepository.save(newSlot);
            
    //         // Update booking
    //         booking.setTimeSlot(newSlot);
    //         booking.setInterviewer(newSlot.getInterviewer());
            
    //         return bookingRepository.save(booking);
            
    //     } catch (OptimisticLockException e) {
    //         throw new RuntimeException(
    //             "Rescheduling failed. Please try again.");
    //     }
    // }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking updateBooking(Long bookingId, Long newSlotId) {

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Can only reschedule confirmed bookings");
        }

        // Lock old slot
        TimeSlot oldSlot = slotRepository
            .findByIdWithLock(booking.getTimeSlot().getId())
            .orElseThrow(() -> new RuntimeException("Old slot not found"));

        // Lock new slot
        TimeSlot newSlot = slotRepository
            .findByIdWithLock(newSlotId)
            .orElseThrow(() -> new RuntimeException("New slot not found"));

        if (!"AVAILABLE".equals(newSlot.getStatus())) {
            throw new RuntimeException("New slot is not available");
        }

        try {
            // Release old slot
            oldSlot.setStatus("AVAILABLE");

            // Book new slot
            newSlot.setStatus("BOOKED");

            // Update booking
            booking.setTimeSlot(newSlot);
            booking.setInterviewer(newSlot.getInterviewer());

            // Save happens at transaction commit
            return booking;

        } catch (Exception e) {
            throw new RuntimeException("Rescheduling failed. Please try again.");
        }
    }

    
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Release the slot
        TimeSlot slot = booking.getTimeSlot();
        slot.setStatus("AVAILABLE");
        slotRepository.save(slot);
        
        // Cancel booking
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
    
    public List<Booking> getBookingsByInterviewer(Long interviewerId) {
        return bookingRepository.findByInterviewerIdAndStatusOrderByBookedAtDesc(
            interviewerId, "CONFIRMED");
    }
    
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
}