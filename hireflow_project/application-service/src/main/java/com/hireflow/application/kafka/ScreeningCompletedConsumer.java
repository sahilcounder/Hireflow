package com.hireflow.application.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireflow.application.model.Application;
import com.hireflow.application.model.ApplicationStatus;
import com.hireflow.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScreeningCompletedConsumer {

    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "screening-completed", groupId = "application-group")
    public void onScreeningCompleted(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            Long applicationId = node.get("applicationId").asLong();
            int score = node.get("score").asInt();

            Application app = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));
            app.setAiScore(score);
            app.setStatus(ApplicationStatus.SCREENING);
            app.setUpdatedAt(LocalDateTime.now());
            applicationRepository.save(app);
            log.info("Updated applicationId={} with aiScore={}", applicationId, score);
        } catch (Exception e) {
            log.error("Failed to process screening-completed: {}", e.getMessage());
        }
    }
}
