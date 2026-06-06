package com.hireflow.application.service;

import com.hireflow.application.client.JobServiceClient;
import com.hireflow.application.dto.ApplicationResponse;
import com.hireflow.application.kafka.ApplicationEventProducer;
import com.hireflow.application.model.Application;
import com.hireflow.application.model.ApplicationStatus;
import com.hireflow.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;
    private final JobServiceClient jobServiceClient;
    private final ApplicationEventProducer eventProducer;

    public ApplicationResponse apply(Long jobId, Long candidateId, String candidateEmail,
                                      MultipartFile resume) throws IOException {
        if (!jobServiceClient.jobExists(jobId)) {
            throw new RuntimeException("Job not found or not open: " + jobId);
        }

        // Save temporarily with placeholder path, then update
        Application app = Application.builder()
                .jobId(jobId)
                .candidateId(candidateId)
                .candidateEmail(candidateEmail)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        app = applicationRepository.save(app);

        // Store file using generated application ID
        String filePath = fileStorageService.store(resume, app.getId());
        app.setResumeFilePath(filePath);
        app = applicationRepository.save(app);

        // Publish to Kafka -> triggers AI Screening + Notification
        eventProducer.publishApplicationSubmitted(
                app.getId(), jobId, candidateId, candidateEmail, filePath);

        return toResponse(app);
    }

    public List<ApplicationResponse> getByJob(Long jobId) {
        return applicationRepository.findByJobIdOrderByAiScoreDesc(jobId)
                .stream().map(this::toResponse).toList();
    }

    public List<ApplicationResponse> getMy(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId)
                .stream().map(this::toResponse).toList();
    }

    public ApplicationResponse updateStatus(Long applicationId, String status, Long recruiterId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));
        app.setStatus(ApplicationStatus.valueOf(status));
        app.setUpdatedAt(LocalDateTime.now());
        applicationRepository.save(app);

        // Notify candidate of status change
        eventProducer.publishStatusChanged(app.getId(), app.getCandidateEmail(), status, "Job #" + app.getJobId());

        return toResponse(app);
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId()).jobId(app.getJobId()).candidateId(app.getCandidateId())
                .candidateEmail(app.getCandidateEmail()).status(app.getStatus())
                .aiScore(app.getAiScore()).appliedAt(app.getAppliedAt()).updatedAt(app.getUpdatedAt())
                .build();
    }
}
