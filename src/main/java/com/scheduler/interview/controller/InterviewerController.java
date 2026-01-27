package com.scheduler.interview.controller;

import com.scheduler.interview.model.Interviewer;
import com.scheduler.interview.service.InterviewerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/interviewers")
@CrossOrigin(origins = "*")
public class InterviewerController {
    
    private final InterviewerService service;
    
    public InterviewerController(InterviewerService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<Interviewer> create(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        Integer maxWeekly = (Integer) request.get("maxWeeklyInterviews");
        
        Interviewer interviewer = service.create(name, email, maxWeekly);
        return ResponseEntity.ok(interviewer);
    }
    
    @GetMapping
    public ResponseEntity<List<Interviewer>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Interviewer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}