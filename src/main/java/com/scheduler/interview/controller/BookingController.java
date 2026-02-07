package com.scheduler.interview.controller;

import com.scheduler.interview.model.Booking;
import com.scheduler.interview.model.Candidate;
import com.scheduler.interview.repository.BookingRepository;
import com.scheduler.interview.repository.CandidateRepository;
import com.scheduler.interview.service.BookingService;

import org.springframework.dao.DataIntegrityViolationException;
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
    private final BookingRepository bookingRepository;
    
    public BookingController(BookingService bookingService, CandidateRepository candidateRepository, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.candidateRepository = candidateRepository;
        this.bookingRepository = bookingRepository;
    }
    
    // @PostMapping("/candidates")
    // public ResponseEntity<?> registerCandidate(
    //         @RequestBody Map<String, String> request) {

    //     String email = request.get("email");

    //     Candidate candidate = new Candidate(
    //         request.get("name"),
    //         email,
    //         request.get("phone")
    //     );

    //     try {
    //         Candidate savedCandidate = candidateRepository.save(candidate);
    //         return ResponseEntity.ok(savedCandidate);

    //     } catch (DataIntegrityViolationException ex) {

    //         // Candidate already exists → fetch from DB
    //         Optional<Candidate> existingCandidateOpt =
    //                 candidateRepository.findByEmail(email);

    //         if (existingCandidateOpt.isEmpty()) {
    //             // very rare edge case
    //             return ResponseEntity
    //                     .status(HttpStatus.CONFLICT)
    //                     .body("Candidate already exists, but could not be fetched");
    //         }

    //         Candidate existingCandidate = existingCandidateOpt.get();
    //         Booking booking = existingCandidate.getBooking();

    //         Map<String, Object> response = new HashMap<>();
    //         response.put("candidate", existingCandidate);

    //         if (booking != null) {
    //             response.put("booking", booking);
    //             response.put("message", "Candidate already registered and has an active booking");
    //         } else {
    //             response.put("booking", null);
    //             response.put("message", "Candidate already registered but has no booking");
    //         }

    //         return ResponseEntity
    //                 .status(HttpStatus.OK)
    //                 .body(response);
    //     }
    // }
    @PostMapping("/candidates")
    public ResponseEntity<?> registerCandidate(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        Candidate candidate = new Candidate(
                request.get("name"),
                email,
                request.get("phone")
        );

        try {
            Candidate savedCandidate = candidateRepository.save(candidate);
            return ResponseEntity.ok(savedCandidate);

        } catch (DataIntegrityViolationException ex) {

            Optional<Candidate> existingCandidateOpt =
                    candidateRepository.findByEmail(email);

            if (existingCandidateOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Candidate already exists, but could not be fetched");
            }

            Candidate existingCandidate = existingCandidateOpt.get();

            System.out.println("Candidate info: " + existingCandidate.getId() + "\n");

            List<Booking> bookings =
                bookingRepository.findAllByCandidateIdRaw(existingCandidate.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("candidate", existingCandidate);

            if (!bookings.isEmpty()) {
                response.put("booking", bookings.get(0));
                response.put(
                        "message",
                        "Candidate already registered and has a booking"
                );
            } else {
                response.put("booking", null);
                response.put(
                        "message",
                        "Candidate already registered but has no booking"
                );
            }

            return ResponseEntity.ok(response);
        }
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