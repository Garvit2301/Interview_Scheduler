package com.scheduler.interview.controller;

import com.scheduler.interview.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminController - Administrative operations
 * Provides endpoints for database management
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * Reset database - DELETE ALL DATA
     * WARNING: This operation cannot be undone!
     * 
     * @return Result of reset operation with count of deleted records
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetDatabase() {
        
        AdminService.ResetResult result = adminService.resetDatabase();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("deletedRecords", Map.of(
            "bookings", result.getBookingsDeleted(),
            "timeSlots", result.getTimeSlotsDeleted(),
            "candidates", result.getCandidatesDeleted(),
            "interviewers", result.getInterviewersDeleted(),
            "total", result.getTotalDeleted()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get current database statistics
     * Shows count of records in each table
     * 
     * @return Current record counts
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        
        AdminService.DatabaseStats stats = adminService.getDatabaseStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentRecords", Map.of(
            "bookings", stats.getTotalBookings(),
            "timeSlots", stats.getTotalTimeSlots(),
            "candidates", stats.getTotalCandidates(),
            "interviewers", stats.getTotalInterviewers(),
            "total", stats.getTotalRecords()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint for admin operations
     * 
     * @return Status of admin service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Admin Service");
        return ResponseEntity.ok(response);
    }
}