package com.scheduler.interview.service;

import com.scheduler.interview.model.Interviewer;
import com.scheduler.interview.model.TimeSlot;
import com.scheduler.interview.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {
    
    private final TimeSlotRepository repository;
    private final InterviewerService interviewerService;
    
    public SlotService(TimeSlotRepository repository, 
                      InterviewerService interviewerService) {
        this.repository = repository;
        this.interviewerService = interviewerService;
    }
    
    @Transactional
    public List<TimeSlot> generateSlots(Long interviewerId, 
                                       String dayOfWeek, 
                                       String startTime, 
                                       String endTime, 
                                       Integer duration) {
        Interviewer interviewer = interviewerService.getById(interviewerId);
        List<TimeSlot> slots = new ArrayList<>();
        
        DayOfWeek day = DayOfWeek.valueOf(dayOfWeek);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        
        // Generate for next 2 weeks
        LocalDate today = LocalDate.now();
        for (int week = 0; week < 2; week++) {
            LocalDate targetDate = today.plusWeeks(week);
            
            // Find next occurrence of the day
            while (targetDate.getDayOfWeek() != day) {
                targetDate = targetDate.plusDays(1);
            }
            
            // Generate time slots for this day
            LocalTime current = start;
            while (current.plusMinutes(duration).isBefore(end) || 
                   current.plusMinutes(duration).equals(end)) {
                
                LocalDateTime slotDateTime = LocalDateTime.of(targetDate, current);
                
                // Only create future slots
                if (slotDateTime.isAfter(LocalDateTime.now())) {
                    TimeSlot slot = new TimeSlot();
                    slot.setInterviewer(interviewer);
                    slot.setSlotDateTime(slotDateTime);
                    slot.setDurationMinutes(duration);
                    slot.setStatus("AVAILABLE");
                    slots.add(slot);
                }
                
                current = current.plusMinutes(duration);
            }
        }
        
        return repository.saveAll(slots);
    }
    
    public List<TimeSlot> getAvailableSlots() {
        return repository.findByStatusAndSlotDateTimeAfterOrderBySlotDateTimeAsc(
            "AVAILABLE", LocalDateTime.now());
    }
    
    public List<TimeSlot> getSlotsByInterviewer(Long interviewerId, 
                                                LocalDateTime start, 
                                                LocalDateTime end) {
        return repository.findByInterviewerIdAndSlotDateTimeBetween(
            interviewerId, start, end);
    }
}