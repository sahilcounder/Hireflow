package com.hireflow.job.service;

import com.hireflow.job.dto.JobRequest;
import com.hireflow.job.dto.JobResponse;
import com.hireflow.job.model.Job;
import com.hireflow.job.model.JobStatus;
import com.hireflow.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public JobResponse createJob(JobRequest req, Long recruiterId) {
        Job job = Job.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .skills(req.getSkills())
                .experience(req.getExperience())
                .location(req.getLocation())
                .salaryRange(req.getSalaryRange())
                .recruiterId(recruiterId)
                .deadline(req.getDeadline())
                .createdAt(LocalDateTime.now())
                .build();
        return toResponse(jobRepository.save(job));
    }

    public List<JobResponse> getAllOpenJobs() {
        return jobRepository.findByStatus(JobStatus.OPEN).stream().map(this::toResponse).toList();
    }

    public JobResponse getJobById(Long id) {
        return jobRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
    }

    public JobResponse updateJob(Long id, JobRequest req, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized to update this job");
        }
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setSkills(req.getSkills());
        job.setExperience(req.getExperience());
        job.setLocation(req.getLocation());
        job.setSalaryRange(req.getSalaryRange());
        job.setDeadline(req.getDeadline());
        return toResponse(jobRepository.save(job));
    }

    public void closeJob(Long id, Long recruiterId, String role) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        if (!role.equals("ADMIN") && !job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Not authorized");
        }
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId()).title(job.getTitle()).description(job.getDescription())
                .skills(job.getSkills()).experience(job.getExperience()).location(job.getLocation())
                .salaryRange(job.getSalaryRange()).recruiterId(job.getRecruiterId())
                .status(job.getStatus()).createdAt(job.getCreatedAt()).deadline(job.getDeadline())
                .build();
    }
}
