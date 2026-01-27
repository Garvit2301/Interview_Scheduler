package com.scheduler.interview.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    private Interviewer interviewer;
    
    @Column(name = "slot_date_time", nullable = false)
    private LocalDateTime slotDateTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60;
    
    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, BOOKED, CANCELLED
    
    @Version
    private Integer version = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
    
    // Constructors
    public TimeSlot() {}
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Interviewer getInterviewer() { return interviewer; }
    public void setInterviewer(Interviewer interviewer) { this.interviewer = interviewer; }
    public LocalDateTime getSlotDateTime() { return slotDateTime; }
    public void setSlotDateTime(LocalDateTime dt) { this.slotDateTime = dt; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer duration) { this.durationMinutes = duration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}