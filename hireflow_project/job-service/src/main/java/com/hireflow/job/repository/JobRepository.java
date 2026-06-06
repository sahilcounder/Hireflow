package com.hireflow.job.repository;

import com.hireflow.job.model.Job;
import com.hireflow.job.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);
    List<Job> findByRecruiterId(Long recruiterId);
}
