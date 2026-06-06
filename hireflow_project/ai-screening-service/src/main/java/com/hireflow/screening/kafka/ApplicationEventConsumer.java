package com.hireflow.screening.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireflow.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventConsumer {

    private final ScreeningService screeningService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "application-submitted", groupId = "screening-group")
    public void onApplicationSubmitted(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            Long applicationId = node.get("applicationId").asLong();
            Long jobId = node.get("jobId").asLong();
            String candidateEmail = node.get("candidateEmail").asText();
            String resumeFilePath = node.get("resumeFilePath").asText();

            log.info("Received application-submitted: applicationId={}", applicationId);
            screeningService.screen(applicationId, jobId, candidateEmail, resumeFilePath);
        } catch (Exception e) {
            log.error("Failed to process application-submitted: {}", e.getMessage());
        }
    }
}
