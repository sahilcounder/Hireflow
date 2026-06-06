package com.hireflow.job.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String skills;
    private String experience;
    private String location;
    private String salaryRange;
    private LocalDateTime deadline;
}
