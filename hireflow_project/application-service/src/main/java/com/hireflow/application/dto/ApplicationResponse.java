package com.hireflow.application.dto;

import com.hireflow.application.model.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private Long candidateId;
    private String candidateEmail;
    private ApplicationStatus status;
    private Integer aiScore;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
