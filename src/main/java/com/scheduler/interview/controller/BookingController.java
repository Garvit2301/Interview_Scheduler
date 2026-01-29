package com.scheduler.interview.controller;

import com.scheduler.interview.model.Booking;
import com.scheduler.interview.model.Candidate;
import com.scheduler.interview.repository.CandidateRepository;
import com.scheduler.interview.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    private final BookingService bookingService;
    private final CandidateRepository candidateRepository;
    
    public BookingController(BookingService bookingService,
                            CandidateRepository candidateRepository) {
        this.bookingService = bookingService;
        this.candidateRepository = candidateRepository;
    }
    
    @PostMapping("/candidates")
    public ResponseEntity<Candidate> registerCandidate(
            @RequestBody Map<String, String> request) {
        
        Candidate candidate = new Candidate(
            request.get("name"),
            request.get("email"),
            request.get("phone"),
            request.get("password")
        );
        
        return ResponseEntity.ok(candidateRepository.save(candidate));
    }

    @PostMapping("/loginCandidates")
    public ResponseEntity<LoginStatus> loginCandidate(
            @RequestBody Map<String, String> request) {
     
        LoginStatus loginstatus = new LoginStatus(
            request.get("email"),
            request.get("password")
        );
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> bookSlot(
            @RequestBody Map<String, Object> request) {
        
        try {
            Long candidateId = ((Number) request.get("candidateId")).longValue();
            Long slotId = ((Number) request.get("slotId")).longValue();
            String notes = (String) request.get("notes");
            
            Booking booking = bookingService.bookSlot(candidateId, slotId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("booking", booking);
            response.put("message", "Booking confirmed successfully!");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> rescheduleBooking(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        try {
            Long newSlotId = ((Number) request.get("newSlotId")).longValue();
            Booking booking = bookingService.updateBooking(id, newSlotId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("booking", booking);
            response.put("message", "Booking rescheduled successfully!");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Booking cancelled successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/interviewer/{id}")
    public ResponseEntity<List<Booking>> getBookingsByInterviewer(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingsByInterviewer(id));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }
}