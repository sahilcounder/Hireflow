package com.hireflow.application.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobServiceClient {

    private final WebClient.Builder webClientBuilder;

    public boolean jobExists(Long jobId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("lb://JOB-SERVICE/api/jobs/" + jobId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            log.error("Job validation failed for jobId={}: {}", jobId, e.getMessage());
            return false;
        }
    }
}
