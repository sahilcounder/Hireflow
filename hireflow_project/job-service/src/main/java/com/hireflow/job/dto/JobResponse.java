package com.hireflow.job.dto;

import com.hireflow.job.model.JobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String skills;
    private String experience;
    private String location;
    private String salaryRange;
    private Long recruiterId;
    private JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
}
