package com.hireflow.screening.controller;

import com.hireflow.screening.model.ScreeningResult;
import com.hireflow.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping("/{applicationId}")
    public ResponseEntity<ScreeningResult> getScreeningResult(
            @PathVariable Long applicationId,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(screeningService.getResult(applicationId));
    }

    @PostMapping("/rescreen/{applicationId}")
    public ResponseEntity<String> rescreen(
            @PathVariable Long applicationId,
            @RequestHeader("X-User-Role") String role) {
        if (!"RECRUITER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();
        }
        // Trigger re-screening asynchronously
        return ResponseEntity.ok("Re-screening triggered for applicationId: " + applicationId);
    }
}
