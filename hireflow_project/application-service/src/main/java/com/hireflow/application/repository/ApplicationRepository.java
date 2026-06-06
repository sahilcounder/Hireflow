package com.hireflow.application.repository;

import com.hireflow.application.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobIdOrderByAiScoreDesc(Long jobId);
    List<Application> findByCandidateId(Long candidateId);
}
