// package com.scheduler.interview.model;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "bookings")
// public class Booking {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @ManyToOne
//     @JoinColumn(name = "candidate_id", nullable = false)
//     private Candidate candidate;
    
//     @ManyToOne
//     @JoinColumn(name = "time_slot_id", nullable = false)
//     private TimeSlot timeSlot;
    
//     @ManyToOne
//     @JoinColumn(name = "interviewer_id", nullable = false)
//     private Interviewer interviewer;
    
//     @Column(nullable = false)
//     private String status = "CONFIRMED"; // CONFIRMED, CANCELLED
    
//     @Column(columnDefinition = "TEXT")
//     private String notes;
    
//     @Column(name = "booked_at")
//     private LocalDateTime bookedAt;
    
//     @PrePersist
//     protected void onCreate() { bookedAt = LocalDateTime.now(); }
    
//     // Constructors
//     public Booking() {}
    
//     // Getters & Setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
//     public Candidate getCandidate() { return candidate; }
//     public void setCandidate(Candidate candidate) { this.candidate = candidate; }
//     public TimeSlot getTimeSlot() { return timeSlot; }
//     public void setTimeSlot(TimeSlot slot) { this.timeSlot = slot; }
//     public Interviewer getInterviewer() { return interviewer; }
//     public void setInterviewer(Interviewer interviewer) { this.interviewer = interviewer; }
//     public String getStatus() { return status; }
//     public void setStatus(String status) { this.status = status; }
//     public String getNotes() { return notes; }
//     public void setNotes(String notes) { this.notes = notes; }
//     public LocalDateTime getBookedAt() { return bookedAt; }
// }





package com.scheduler.interview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ONE booking → ONE candidate
    @OneToOne
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;

    @Column(nullable = false)
    private String status = "CONFIRMED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "booked_at", nullable = false, updatable = false)
    private LocalDateTime bookedAt;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }

    public Booking() {}

    public Long getId() { return id; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Interviewer getInterviewer() { return interviewer; }
    public void setInterviewer(Interviewer interviewer) { this.interviewer = interviewer; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getBookedAt() { return bookedAt; }
}
