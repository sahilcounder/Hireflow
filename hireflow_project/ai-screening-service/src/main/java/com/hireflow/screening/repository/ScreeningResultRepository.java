package com.hireflow.screening.repository;

import com.hireflow.screening.model.ScreeningResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScreeningResultRepository extends MongoRepository<ScreeningResult, String> {
    Optional<ScreeningResult> findByApplicationId(Long applicationId);
}
