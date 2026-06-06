package com.hireflow.application.dto;

import lombok.Data;

@Data
public class ApplicationRequest {
    private Long jobId;
    // resume file comes as MultipartFile, not in this DTO
}
