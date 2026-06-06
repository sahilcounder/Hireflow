package com.hireflow.application.controller;

import com.hireflow.application.dto.ApplicationResponse;
import com.hireflow.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Candidate applies: POST /api/applications
     * Body: multipart/form-data with jobId + resume file
     */
    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(
            @RequestParam Long jobId,
            @RequestParam MultipartFile resume,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) throws IOException {

        if (!"CANDIDATE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(jobId, userId, email, resume));
    }

    /**
     * Recruiter views all applicants for a job, sorted by AI score
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getByJob(
            @PathVariable Long jobId,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.getByJob(jobId));
    }

    /**
     * Candidate views their own applications
     */
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMy(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(applicationService.getMy(userId));
    }

    /**
     * Recruiter shortlists / rejects / schedules interview
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(applicationService.updateStatus(id, status, userId));
    }
}
