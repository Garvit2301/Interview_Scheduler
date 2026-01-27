package com.scheduler.interview.controller;

import com.scheduler.interview.model.TimeSlot;
import com.scheduler.interview.service.SlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin(origins = "*")
public class SlotController {
    
    private final SlotService service;
    
    public SlotController(SlotService service) {
        this.service = service;
    }
    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateSlots(
            @RequestBody Map<String, Object> request) {
        
        Long interviewerId = ((Number) request.get("interviewerId")).longValue();
        String dayOfWeek = (String) request.get("dayOfWeek");
        String startTime = (String) request.get("startTime");
        String endTime = (String) request.get("endTime");
        Integer duration = (Integer) request.get("duration");
        
        List<TimeSlot> slots = service.generateSlots(
            interviewerId, dayOfWeek, startTime, endTime, duration);
        
        Map<String, Object> response = new HashMap<>();
        response.put("generated", slots.size());
        response.put("message", "Successfully generated " + slots.size() + " slots");
        response.put("slots", slots);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableSlots() {
        List<TimeSlot> slots = service.getAvailableSlots();
        
        Map<String, Object> response = new HashMap<>();
        response.put("slots", slots);
        response.put("count", slots.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/interviewer/{id}")
    public ResponseEntity<List<TimeSlot>> getByInterviewer(
            @PathVariable Long id,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        
        LocalDateTime startDate = start != null ? 
            LocalDateTime.parse(start) : LocalDateTime.now();
        LocalDateTime endDate = end != null ? 
            LocalDateTime.parse(end) : LocalDateTime.now().plusWeeks(2);
        
        return ResponseEntity.ok(
            service.getSlotsByInterviewer(id, startDate, endDate));
    }
}