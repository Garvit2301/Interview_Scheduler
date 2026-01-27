package com.scheduler.interview.service;

import com.scheduler.interview.model.Interviewer;
import com.scheduler.interview.repository.InterviewerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InterviewerService {
    
    private final InterviewerRepository repository;
    
    public InterviewerService(InterviewerRepository repository) {
        this.repository = repository;
    }
    
    @Transactional
    public Interviewer create(String name, String email, Integer maxWeekly) {
        Interviewer interviewer = new Interviewer(name, email, maxWeekly);
        return repository.save(interviewer);
    }
    
    public List<Interviewer> getAll() {
        return repository.findAll();
    }
    
    public Interviewer getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Interviewer not found: " + id));
    }
}